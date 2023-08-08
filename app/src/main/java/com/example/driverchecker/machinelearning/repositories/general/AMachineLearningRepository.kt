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

    protected val mEvaluationFlowState: MutableSharedFlow<LiveEvaluationStateInterface> = MutableSharedFlow(replay = 1, extraBufferCapacity = 5, onBufferOverflow = BufferOverflow.DROP_OLDEST)
    protected var liveEvaluationJob: Job? = null
    protected var loadingModelJob: Job? = null

    // TODO: The scope must be external (from the Application level or Activity level)
    override val repositoryScope = CoroutineScope(SupervisorJob())

    override val evaluationFlowState: SharedFlow<LiveEvaluationStateInterface>?
        get() = mEvaluationFlowState.asSharedFlow()

    init {
        mEvaluationFlowState.tryEmit(LiveEvaluationState.Ready(false))
        listenOnLoadingState()
    }

    protected open fun listenOnLoadingState () {
        loadingModelJob = repositoryScope.launch {
            model?.isLoaded?.collect { state ->
                if (mEvaluationFlowState.replayCache.last() == LiveEvaluationState.Ready(!state))
                    mEvaluationFlowState.emit(LiveEvaluationState.Ready(state))
            }
        }
    }

    override suspend fun instantEvaluation(input: D): R? {
        var result: R? = null
        val job = repositoryScope.launch(Dispatchers.Default) { result = model?.processAndEvaluate(input) }
        job.join()

        return result
    }

    override suspend fun continuousEvaluation(input: Flow<D>, scope: CoroutineScope): R? {
        jobEvaluation(input, scope).join()

        return window.lastResult
    }

    protected open fun jobEvaluation (input: Flow<D>, scope: CoroutineScope): Job {
        return repositoryScope.launch(Dispatchers.Default) {
            // check if the repo is ready to make evaluations
            if (mEvaluationFlowState.replayCache.last() == LiveEvaluationState.Ready(true)) {
                mEvaluationFlowState.emit(LiveEvaluationState.Start)

                flowEvaluation(input, ::cancel)?.collect()
            } else {
                mEvaluationFlowState.emit(LiveEvaluationState.End(Throwable("The stream is not ready yet"), null))
                mEvaluationFlowState.emit(LiveEvaluationState.Ready(model?.isLoaded?.value ?: false))
            }
        }
    }

    protected open fun flowEvaluation (
        input: Flow<D>,
        onConditionSatisfied: (CancellationException) -> Unit
    ): Flow<R>? {
        return model?.processAndEvaluatesStream(input)
                    ?.onEach { postProcessedResult -> onEachEvaluation(postProcessedResult, onConditionSatisfied) }
                    ?.cancellable()
                    ?.catch { cause -> onErrorEvaluation(cause) }
                    ?.onCompletion { cause -> onCompletionEvaluation (cause) }
    }

    protected open suspend fun onCompletionEvaluation (cause: Throwable?) {
        Log.d("JobClassification", "finally finished")

        if (cause != null && cause !is CorrectCancellationException) {
            mEvaluationFlowState.emit(
                LiveEvaluationState.End(cause, null)
            )
        } else {
            mEvaluationFlowState.emit(
                LiveEvaluationState.End(null, window.getFinalResults())
            )
        }

        window.clean()

        mEvaluationFlowState.emit(LiveEvaluationState.Ready(model?.isLoaded?.value ?: false))
    }

    protected open suspend fun onEachEvaluation (
        postProcessedResult: R,
        onConditionSatisfied: (CancellationException) -> Unit
    ) {
        Log.d("JobClassification", "finally finished")
        window.next(postProcessedResult)

        if (window.hasAcceptedLast) {
            mEvaluationFlowState.emit(
                LiveEvaluationState.Loading(window.totEvaluationsDone, window.lastResult)
            )
        }

        // TODO: Pass the metrics and R
        if (window.isSatisfied())
            onConditionSatisfied(CorrectCancellationException())
    }

    protected open suspend fun onErrorEvaluation (cause: Throwable) {
        Log.e("JobClassification", "Just caught this: ${cause.message}")
    }

    override fun onStartLiveEvaluation(
        input: SharedFlow<D>,
        scope: CoroutineScope
    ) {
        if (mEvaluationFlowState.replayCache.last() == LiveEvaluationState.Ready(true)) {
            liveEvaluationJob = jobEvaluation(input.buffer(2), scope)
        }
    }

    override fun onStopLiveEvaluation(externalCause: CancellationException?) {
        liveEvaluationJob?.cancel(cause = externalCause ?: ExternalCancellationException())
    }

    override fun <ModelInit> updateModel(init: ModelInit) {
        model?.loadModel(init)
    }

//    protected open inner class
}