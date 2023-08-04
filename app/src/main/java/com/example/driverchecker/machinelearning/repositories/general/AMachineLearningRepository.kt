package com.example.driverchecker.machinelearning.repositories.general

import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.models.IMachineLearningModel
import com.example.driverchecker.machinelearning.helpers.windows.IMachineLearningWindow
import com.example.driverchecker.machinelearning.helpers.windows.MachineLearningWindow
import com.example.driverchecker.machinelearning.repositories.IMachineLearningRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

open class AMachineLearningRepository<D, R : WithConfidence> (importedModel: IMachineLearningModel<D, R>?) :
    IMachineLearningRepository<D, R> {
    protected open val window: IMachineLearningWindow<R> = MachineLearningWindow()
    protected val _externalProgressState: MutableSharedFlow<LiveEvaluationStateInterface<R>> = MutableSharedFlow(replay = 1, extraBufferCapacity = 5, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    protected var liveClassificationJob: Job? = null
    protected var loadingModelJob: Job? = null
    protected open var model: IMachineLearningModel<D, R>? = importedModel

    override val repositoryScope = CoroutineScope(SupervisorJob())

    override val analysisProgressState: SharedFlow<LiveEvaluationStateInterface<R>>?
        get() = _externalProgressState.asSharedFlow()

    init {
        _externalProgressState.tryEmit(LiveEvaluationState.Ready(false))
        listenOnLoadingState()
    }

    protected open fun listenOnLoadingState () {
        loadingModelJob = repositoryScope.launch {
            model?.isLoaded?.collect { state ->
                if (_externalProgressState.replayCache.last() == LiveEvaluationState.Ready(!state))
                    _externalProgressState.emit(LiveEvaluationState.Ready(state))
            }
        }
    }

    override suspend fun instantClassification(input: D): R? {
        var result: R? = null
        val job = repositoryScope.launch(Dispatchers.Default) { result = model?.processAndEvaluate(input) }
        job.join()

        return result
    }

    override suspend fun continuousClassification(input: List<D>): R? {
        return withContext(Dispatchers.Default) {
            var result: R? = null

            try {
                for (data in input) {
                    val instantResult : R = model?.processAndEvaluate(data) ?: throw Error("The result is null")

                    window.next(instantResult)
                    // TODO: Pass the metrics and R
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

    override suspend fun continuousClassification(input: Flow<D>, scope: CoroutineScope): R? {
        jobClassification(input, scope).join()

        return window.getLastResult()
    }

    protected open fun jobClassification (input: Flow<D>, scope: CoroutineScope): Job {
        return repositoryScope.launch(Dispatchers.Default) {
            // check if the repo is ready to make evaluations
            if (_externalProgressState.replayCache.last() == LiveEvaluationState.Ready(true)) {
                _externalProgressState.emit(LiveEvaluationState.Start)

                flowClassification(input, ::cancel)?.collect()
            } else {
                _externalProgressState.emit(LiveEvaluationState.End(Throwable("The stream is not ready yet"), null))
                _externalProgressState.emit(LiveEvaluationState.Ready(model?.isLoaded?.value ?: false))
            }
        }
    }

    protected open fun flowClassification (input: Flow<D>, onConditionSatisfied: (CancellationException) -> Unit): Flow<R>? {
        return model?.processAndEvaluatesStream(input)
                    ?.onEach {
                        window.next(it)

                        _externalProgressState.emit(
                            LiveEvaluationState.Loading(
                                window.getIndex(),
                                window.getLastResult()
                            ))
                        // TODO: Pass the metrics and R
                        if (window.isSatisfied())
                            onConditionSatisfied(CorrectCancellationException())

                        Log.d("JobClassification", "Checked: ${window.getIndex()} with ${window.getLastResult()}")
                    }
                    ?.cancellable()
                    ?.catch { cause ->
                        Log.e("JobClassification", "Just caught this: ${cause.message}")
                    }
                    ?.onCompletion { cause ->
                        Log.d("JobClassification", "finally finished")

                        if (cause != null && cause !is CorrectCancellationException) {
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
    }

    override fun onStartLiveClassification(
        input: SharedFlow<D>,
        scope: CoroutineScope
    ) {
        if (_externalProgressState.replayCache.last() == LiveEvaluationState.Ready(true)) {
            liveClassificationJob = jobClassification(input.buffer(2), scope)
        }
    }

    override fun onStopLiveClassification(externalCause: CancellationException?) {
        liveClassificationJob?.cancel(cause = externalCause ?: ExternalCancellationException())
    }

    override fun <ModelInit> updateModel(init: ModelInit) {
        model?.loadModel(init)
    }
}