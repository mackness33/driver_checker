package com.example.driverchecker.machinelearning.repositories.general

import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.listeners.IGenericListener
import com.example.driverchecker.machinelearning.helpers.listeners.ClientStateListener
import com.example.driverchecker.machinelearning.helpers.listeners.GenericListener
import com.example.driverchecker.machinelearning.models.IMachineLearningModel
import com.example.driverchecker.machinelearning.helpers.windows.IMachineLearningWindow
import com.example.driverchecker.machinelearning.repositories.IMachineLearningRepository
import com.example.driverchecker.utils.AtomicValue
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

abstract class AMachineLearningRepository<I, O : WithConfidence, FR: WithConfidence> () :
    IMachineLearningRepository<I, O, FR> {
    // abstracted
    protected abstract val window: IMachineLearningWindow<O>
    protected abstract val model: IMachineLearningModel<I, O>?
    protected abstract var clientListener: ClientStateListener?
    protected abstract var modelListener: IGenericListener<Boolean>?

    protected val mEvaluationFlowState: MutableSharedFlow<LiveEvaluationStateInterface> = MutableSharedFlow(
        replay = 1,
        extraBufferCapacity = 5,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    protected var liveEvaluationJob: Job? = null
    protected var loadingModelJob: Job? = null


    // TODO: The scope must be external (from the Application level or Activity level)
    override val repositoryScope = CoroutineScope(SupervisorJob())

    override val evaluationFlowState: SharedFlow<LiveEvaluationStateInterface>?
        get() = mEvaluationFlowState.asSharedFlow()

    init {
        mEvaluationFlowState.tryEmit(LiveEvaluationState.Ready(false))
        startListenModelState()
//        listenModelState ()
    }

    private fun startListenModelState() = modelListener?.listen(repositoryScope, model?.isLoaded)

    protected open fun listenModelState () {
        loadingModelJob = repositoryScope.launch {
            model?.isLoaded?.collect { state ->
                if (mEvaluationFlowState.replayCache.last() == LiveEvaluationState.Ready(!state))
                    mEvaluationFlowState.emit(LiveEvaluationState.Ready(state))
            }
        }
    }

    protected open fun isReady() : Boolean? {
        return (modelListener?.currentState?.value ?: false) && clientListener?.currentState?.value == ClientState.Ready
    }

    protected open suspend fun triggerReadyState() {
        // if the last state of the evaluation is different from the ready state that has been triggered
        // then send the new ready state and stop all the job that were running
        if (mEvaluationFlowState.replayCache.last() != LiveEvaluationState.Ready(isReady() == true)){
            liveEvaluationJob?.cancel(InternalCancellationException())
            mEvaluationFlowState.emit(LiveEvaluationState.Ready(isReady() == true))
        }
    }

    override suspend fun instantEvaluation(input: I): O? {
        var result: O? = null
        val job = repositoryScope.launch(Dispatchers.Default) { result = model?.processAndEvaluate(input) }
        job.join()

        return result
    }

    override suspend fun continuousEvaluation(input: Flow<I>, scope: CoroutineScope): O? {
        jobEvaluation(input, scope).join()

        return window.lastResult
    }

    protected open fun jobEvaluation (input: Flow<I>, scope: CoroutineScope): Job {
        return repositoryScope.launch(Dispatchers.Default) {
            // check if the repo is ready to make evaluations
            if (mEvaluationFlowState.replayCache.last() == LiveEvaluationState.Ready(true)) {
                mEvaluationFlowState.emit(LiveEvaluationState.Start)

                flowEvaluation(input, ::cancel)?.collect()
            } else {
                mEvaluationFlowState.emit(LiveEvaluationState.End(Throwable("The stream is not ready yet"), null))
//                mEvaluationFlowState.emit(LiveEvaluationState.Ready(model?.isLoaded?.value ?: false))
                triggerReadyState()
            }
        }
    }

    protected open fun flowEvaluation (
        input: Flow<I>,
        onConditionSatisfied: (CancellationException) -> Unit
    ): Flow<O>? {
        return model?.processAndEvaluatesStream(input)
                    ?.onEach { postProcessedResult -> onEachEvaluation(postProcessedResult, onConditionSatisfied) }
                    ?.cancellable()
                    ?.catch { cause -> onErrorEvaluation(cause) }
                    ?.onCompletion { cause -> onCompletionEvaluation (cause) }
    }

    protected open suspend fun onCompletionEvaluation (cause: Throwable?) {
        Log.d("AMLClassification", "finally finished")

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
//        mEvaluationFlowState.emit(LiveEvaluationState.Ready(model?.isLoaded?.value ?: false))
    }

    protected open suspend fun onEachEvaluation (
        postProcessedResult: O,
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
        input: SharedFlow<I>,
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


    fun addClient (
        input: SharedFlow<ClientStateInterface>?
    ) {
        if (input != null)
            clientListener?.listen(repositoryScope, input)
    }

    fun removeClient () {
        clientListener?.destroy()
    }

    protected open inner class ClientListener : ClientStateListener, GenericListener<ClientStateInterface> {
        constructor () : super()

        constructor (scope: CoroutineScope, clientFlow: SharedFlow<ClientStateInterface>) : super(scope, clientFlow)

        override suspend fun onLiveEvaluationReady() {
            triggerReadyState()
        }

        override suspend fun onLiveEvaluationStart(state: ClientState.Start<*>) {
            try {
                val typedState = state as ClientState.Start<I>
                if (mEvaluationFlowState.replayCache.last() == LiveEvaluationState.Ready(true)) {
                    liveEvaluationJob = jobEvaluation(typedState.input.buffer(2), repositoryScope)
                }
            } catch (e : Throwable) {
                mEvaluationFlowState.emit(LiveEvaluationState.End(e, null))
            }
        }

        override fun onLiveEvaluationStop(state: ClientState.Stop) {
            liveEvaluationJob?.cancel(state.cause)
        }
    }

    protected open inner class ModelListener () : GenericListener<Boolean> () {
        override suspend fun collectClientStates (state: Boolean) {
            super.collectClientStates(state)
            triggerReadyState()
        }
    }
}