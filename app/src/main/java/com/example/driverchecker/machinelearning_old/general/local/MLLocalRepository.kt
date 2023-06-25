package com.example.driverchecker.machinelearning_old.general.local

import android.util.Log
import com.example.driverchecker.machinelearning.data.LiveEvaluationState
import com.example.driverchecker.machinelearning.data.LiveEvaluationStateInterface
import com.example.driverchecker.machinelearning.general.MachineLearningWindow
import com.example.driverchecker.machinelearning_old.data.MachineLearningArrayListOutputOld
import com.example.driverchecker.machinelearning_old.general.MLRepositoryInterface
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

abstract class MLLocalRepository <Data, Prediction, Superclass, Result : MachineLearningArrayListOutputOld<Data, Prediction, Superclass>> (protected open val model: MLLocalModel<Data, Result>? = null) :
    MLRepositoryInterface<Data, Result> {
    protected var window: MachineLearningWindow<Data, Result> = MachineLearningWindow()
    protected val _internalanalysisProgressState: MutableStateFlow<LiveEvaluationStateInterface<Result>> = MutableStateFlow(LiveEvaluationState.Ready(false))
    protected val _externalProgressState: MutableSharedFlow<LiveEvaluationStateInterface<Result>> = MutableSharedFlow(replay = 1, extraBufferCapacity = 5, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    protected var liveClassificationJob: Job? = null

    override val repositoryScope = CoroutineScope(SupervisorJob())

    override val analysisProgressState: SharedFlow<LiveEvaluationStateInterface<Result>>?
        get() = _externalProgressState.asSharedFlow()

    init {
        _externalProgressState.tryEmit(LiveEvaluationState.Ready(false))
        listenOnLoadingState()
    }

    protected fun listenOnLoadingState () {
        repositoryScope.launch {
            model?.isLoaded?.collect { state ->
                if (_externalProgressState.replayCache.last() == LiveEvaluationState.Ready(!state))
                    _externalProgressState.emit(LiveEvaluationState.Ready(state))
            }
        }
    }

    override suspend fun instantClassification(input: Data): Result? {
        var result: Result? = null
        val job = repositoryScope.launch(Dispatchers.Default) { result = model?.processAndEvaluate(input) }
        job.join()

        return result
    }

    override suspend fun continuousClassification(input: List<Data>): Result? {
        return withContext(Dispatchers.Default) {
            var result: Result? = null

            try {
                for (data in input) {
                    val instantResult : Result = model?.processAndEvaluate(data) ?: throw Error("The result is null")

                    window.next(instantResult!!)
                    // TODO: Pass the metrics and Result
                    if (window.isSatisfied()) {
                        result = window.lastResult
                        break;
                    }
                }

            } catch (e: Throwable) {
                Log.d("FlowClassificationOutside", "Just caught this, ${e.message}")
            } finally {
                Log.d("FlowClassificationWindow", "finally finished")
                window.clean()
            }

            return@withContext result
        }
    }

    override suspend fun continuousClassification(input: Flow<Data>, scope: CoroutineScope): Result? {
        jobClassification(input, scope).join()

        return window.lastResult
    }

    protected fun jobClassification (input: Flow<Data>, scope: CoroutineScope): Job {
        return repositoryScope.launch(Dispatchers.Default) {
            // check if the repo is ready to make evaluations
            if (_externalProgressState.replayCache.last() == LiveEvaluationState.Ready(true)) {
                _externalProgressState.emit(LiveEvaluationState.Start(null))
                model?.processAndEvaluatesStream(input)
                    ?.onEach {
                        if (it == null) throw Error("The result is null")

                        window.next(it)
                        _externalProgressState.emit(LiveEvaluationState.Loading(
                            window.index,
                            window.lastResult
                        ))
                        // TODO: Pass the metrics and Result
                        if (window.isSatisfied())
                            cancel()

                        Log.d("JobClassification", "Checked: ${window.index} with ${window.lastResult}")
                    }
                    ?.cancellable()
                    ?.catch { cause ->
                        Log.e("JobClassification", "Just caught this, ${cause.message}, cause")
                    }
                    ?.onCompletion { cause ->
                        Log.d("JobClassification", "finally finished")

                        if (cause != null && cause !is CancellationException) {
                            _externalProgressState.emit(
                                LiveEvaluationState.End(cause, null)
                            )
                        } else {
                            _externalProgressState.emit(
                                LiveEvaluationState.End(null, window.lastResult)
                            )
                        }

                        window.clean()

                        _externalProgressState.emit(LiveEvaluationState.Ready(model?.isLoaded?.value ?: false))
                    }
                    ?.collect()

            } else {
                _externalProgressState.emit(LiveEvaluationState.End(Throwable("The stream is not ready yet"), null))
                _externalProgressState.emit(LiveEvaluationState.Ready(model?.isLoaded?.value ?: false))
            }
        }
    }

    override suspend fun onStartLiveClassification(
        input: SharedFlow<Data>,
        scope: CoroutineScope
    ) {
        if (_externalProgressState.replayCache.last() == LiveEvaluationState.Ready(true)) {
            liveClassificationJob = jobClassification(input.buffer(2), scope)
        }
    }

    override suspend fun onStopLiveClassification() {
        liveClassificationJob?.cancel()
    }

    fun updateLocalModel (path: String) {
        model?.loadModel(path)
    }
}