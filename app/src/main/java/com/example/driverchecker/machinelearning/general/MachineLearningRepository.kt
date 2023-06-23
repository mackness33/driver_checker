package com.example.driverchecker.machinelearning.general

import android.net.Uri
import android.util.Log
import com.example.driverchecker.MLWindow
import com.example.driverchecker.MLWindowInterface
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.general.local.LiveEvaluationState
import com.example.driverchecker.machinelearning.general.local.LiveEvaluationStateInterface
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

open class MachineLearningRepository<Data, Prediction, Superclass, Result : MachineLearningArrayListOutput<Data, Prediction, Superclass>> () : IMachineLearningRepository<Data, Result> {
    protected var window: MLWindowInterface<Result> = MLWindow()
    protected val _externalProgressState: MutableSharedFlow<LiveEvaluationStateInterface<Result>> = MutableSharedFlow(replay = 1, extraBufferCapacity = 5, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    protected var liveClassificationJob: Job? = null
    protected open var model: IMachineLearningModel<Data, Result>? = null

    override val repositoryScope = CoroutineScope(SupervisorJob())

    override val analysisProgressState: SharedFlow<LiveEvaluationStateInterface<Result>>?
        get() = _externalProgressState.asSharedFlow()

    constructor (importedModel: IMachineLearningModel<Data, Result>) : this() {
        model = importedModel
    }

    init {
        _externalProgressState.tryEmit(LiveEvaluationState.Ready(false))
        listenOnLoadingState()
    }

    protected open fun listenOnLoadingState () {
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

                    window.next(instantResult)
                    // TODO: Pass the metrics and Result
                    if (window.isSatisfied()) {
                        result = window.getLastResult()
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

        return window.getLastResult()
    }

    protected fun jobClassification (input: Flow<Data>, scope: CoroutineScope): Job {
        return repositoryScope.launch(Dispatchers.Default) {
            // check if the repo is ready to make evaluations
            if (_externalProgressState.replayCache.last() == LiveEvaluationState.Ready(true)) {
                _externalProgressState.emit(LiveEvaluationState.Start(null))

                model?.processAndEvaluatesStream(input)
                    ?.onEach {
                        window.next(it)

                        _externalProgressState.emit(
                            LiveEvaluationState.Loading(
                            window.getIndex(),
                            window.getLastResult()
                        ))
                        // TODO: Pass the metrics and Result
                        if (window.isSatisfied())
                            cancel()

                        Log.d("JobClassification", "Checked: ${window.getIndex()} with ${window.getLastResult()}")
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
                                LiveEvaluationState.End(null, window.getLastResult())
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

    override fun <ModelInit> updateModel(init: ModelInit) {
        model?.loadModel(init)
    }
}