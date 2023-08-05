package com.example.driverchecker.machinelearning.repositories.general

import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.models.IMachineLearningModel
import com.example.driverchecker.machinelearning.helpers.windows.IMachineLearningWindow
import com.example.driverchecker.machinelearning.repositories.IMachineLearningRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

abstract class AMachineLearningRepository<D, R : WithConfidence> () :
    IMachineLearningRepository<D, R> {
    // abstracted
    protected abstract val window: IMachineLearningWindow<R>
    protected abstract val model: IMachineLearningModel<D, R>?

    protected val _externalProgressState: MutableSharedFlow<LiveEvaluationStateInterface> = MutableSharedFlow(replay = 1, extraBufferCapacity = 5, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    protected var liveClassificationJob: Job? = null
    protected var loadingModelJob: Job? = null

    // TODO: The scope must be external (from the Application level or Activity level)
    override val repositoryScope = CoroutineScope(SupervisorJob())

    override val analysisProgressState: SharedFlow<LiveEvaluationStateInterface>?
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

    override suspend fun continuousClassification(input: Flow<D>, scope: CoroutineScope): R? {
        jobClassification(input, scope).join()

        return window.lastResult
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
                    ?.onEach { postProcessedResult ->
                        window.next(postProcessedResult)

                        if (window.hasAcceptedLast) {
                            _externalProgressState.emit(
                                LiveEvaluationState.Loading(window.totEvaluationsDone, window.lastResult)
                            )
                        }

                        // TODO: Pass the metrics and R
                        if (window.isSatisfied())
                            onConditionSatisfied(CorrectCancellationException())

                        Log.d("JobClassification", "Checked: ${window.totEvaluationsDone} with ${window.lastResult}")
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
                                LiveEvaluationState.End(null, window.getFinalResults())
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