package com.example.driverchecker.machinelearning.repositories.general

import android.util.Log
import com.example.driverchecker.machinelearning.collections.MachineLearningWindowsMutableCollection
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.listeners.*
import com.example.driverchecker.machinelearning.helpers.producers.AProducer
import com.example.driverchecker.machinelearning.helpers.producers.ILiveEvaluationProducer
import com.example.driverchecker.machinelearning.models.IMachineLearningModel
import com.example.driverchecker.machinelearning.helpers.windows.IMachineLearningWindow
import com.example.driverchecker.machinelearning.models.IClassificationModel
import com.example.driverchecker.machinelearning.repositories.IMachineLearningRepository
import com.example.driverchecker.utils.MutableObservableData
import com.example.driverchecker.utils.ObservableData
import com.example.driverchecker.utils.StatefulData
import com.example.driverchecker.utils.Timer
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlin.time.ExperimentalTime

abstract class AMachineLearningRepository<I, O : IMachineLearningOutputStats, FR: IMachineLearningFinalResult> (override val repositoryScope: CoroutineScope) :
    IMachineLearningRepository<I, O, FR> {
    protected val producerIsInitialized = CompletableDeferred<Nothing?>()

    open val evaluationStateProducer: ILiveEvaluationProducer<LiveEvaluationStateInterface> = LiveEvaluationProducer()

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
    protected val oldTimer = Timer()
    protected val timer = Timer()
    protected var oldSettings: IOldSettings? = null
    override val availableSettings : ISettings = Settings (
        listOf(1, 3, 5, 10, 20, 30),
        listOf(0.10f, 0.50f, 0.70f, 0.80f, 0.90f, 0.95f),
        listOf("BasicImageDetectionWindow"),
        0.10f
    )
    protected val mSettings: MutableObservableData<ISettings> = StatefulData(Settings(
        availableSettings.multipleWindowsFrames.subList(2,4),
        availableSettings.multipleWindowsThresholds.subList(1,4),
        availableSettings.multipleTypes.subList(0,1),
        0.10f
    ))
    override val settings: ObservableData<ISettings>
        get() = mSettings
    private val privateSettings: ISettings
        get() = mSettings.value
    protected abstract val collectionOfWindows: MachineLearningWindowsMutableCollection<O>

    override val evaluationFlowState: SharedFlow<LiveEvaluationStateInterface>?
        get() = mEvaluationFlowState.asSharedFlow()

    init {
        mEvaluationFlowState.tryEmit(LiveEvaluationState.Ready(false))
    }

    open fun initialize () {
        evaluationStateProducer.initialize()
        collectionOfWindows.initialize(availableSettings)
        collectionOfWindows.updateSettings(privateSettings)
        evaluationStateProducer.tryEmitReady(false)
    }

    override fun updateModelThreshold (threshold: Float) {
        model?.updateThreshold(threshold)
        collectionOfWindows.updateSettings(Settings(
            availableSettings.multipleWindowsFrames.subList(2,4),
            availableSettings.multipleWindowsThresholds.subList(1,4),
            availableSettings.multipleTypes.subList(0,1),
            threshold
        ))
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
//        if (mEvaluationFlowState.replayCache.last() != LiveEvaluationState.Ready(isReady() == true)){
        producerIsInitialized.await()
        if (!evaluationStateProducer.isLast(LiveEvaluationState.Ready(isReady() == true))){
            liveEvaluationJob?.cancel(InternalCancellationException())
            mEvaluationFlowState.emit(LiveEvaluationState.Ready(isReady() == true))
            evaluationStateProducer.emitReady(isReady() == true)
        }
    }

    override suspend fun instantEvaluation(input: I): O? {
        var result: O? = null
        val job = repositoryScope.launch(Dispatchers.Default) { result = model?.processAndEvaluate(input) }
        job.join()

        return result
    }

    override suspend fun continuousEvaluation(input: Flow<I>, settings: IOldSettings): O? {
        jobEvaluation(input, settings).join()

        return window.lastResult
    }

    @OptIn(ExperimentalTime::class)
    protected open fun jobEvaluation (input: Flow<I>, newSettings: IOldSettings): Job {
        return repositoryScope.launch(Dispatchers.Default) {
            // check if the repo is ready to make evaluations
//            if (mEvaluationFlowState.replayCache.last() == LiveEvaluationState.Ready(true)) {
            if (evaluationStateProducer.isLast(LiveEvaluationState.Ready(true))) {
                evaluationStateProducer.emitStart()
                mEvaluationFlowState.emit(LiveEvaluationState.Start)
                timer.markStart()
                /* DELETABLE */
                model?.updateThreshold(newSettings.modelThreshold)
                oldSettings = newSettings

                oldTimer.markStart()

                /* DELETABLE */
                window.initialize(newSettings, oldTimer.start!!)

                flowEvaluation(input, ::cancel)?.collect()
            } else {
                mEvaluationFlowState.emit(LiveEvaluationState.OldEnd(Throwable("The stream is not ready yet"), null))
                evaluationStateProducer.emitErrorEnd(Throwable("The stream is not ready yet"))
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
//                    ?.catch { cause -> onErrorEvaluation(cause) }
                    ?.onCompletion { cause -> onCompletionEvaluation (cause) }
    }

    protected open suspend fun onCompletionEvaluation (cause: Throwable?) {
        Log.d("AMLClassification", "finally finished")

        if (cause != null && cause !is CorrectCancellationException) {
            mEvaluationFlowState.emit(
                LiveEvaluationState.OldEnd(cause, null)
            )
            evaluationStateProducer.emitErrorEnd(cause)
        } else {
            oldTimer.markEnd()
            mEvaluationFlowState.emit(
                LiveEvaluationState.OldEnd(
                    null,
                    OldMachineLearningFinalResult(
                        window.getOldFinalResults(),
                        oldSettings,
                        MachineLearningOldMetrics(window.getOldMetrics())
                    )
                )
            )
            evaluationStateProducer.emitSuccessEnd()
        }

        oldTimer.reset()
        oldSettings = null
        timer.reset()
        window.clean()
        collectionOfWindows.clean()
    }

    protected open suspend fun onEachEvaluation (
        postProcessedResult: O,
        onConditionSatisfied: (CancellationException) -> Unit
    ) {
        Log.d("JobClassification", "finally finished")
        timer.markEnd()
        window.next(postProcessedResult, timer.diff())
        collectionOfWindows.next(postProcessedResult, timer.diff())
        timer.markStart()

        if (collectionOfWindows.hasAcceptedLast) {
            mEvaluationFlowState.emit(
                LiveEvaluationState.Loading(window.totEvaluationsDone, window.lastResult)
            )
            evaluationStateProducer.emitLoading()
        }

        // TODO: Pass the metrics and R
        if (collectionOfWindows.isSatisfied())
            onConditionSatisfied(CorrectCancellationException())
    }

    protected open suspend fun onErrorEvaluation (cause: Throwable) {
        Log.e("JobClassification", "Just caught this: ${cause.message}")
    }

    override fun onStartLiveEvaluation(
        input: SharedFlow<I>
    ) {
        if (mEvaluationFlowState.replayCache.last() == LiveEvaluationState.Ready(true) && liveEvaluationJob?.isCompleted != false) {
            liveEvaluationJob = jobEvaluation(input.buffer(2), OldSettings(1,1f,1f))
        }
    }

    override fun onStopLiveEvaluation(externalCause: CancellationException?) {
//        liveEvaluationJob?.invokeOnCompletion { cause -> runBlocking { onCompletionEvaluation(cause) } }
        liveEvaluationJob?.cancel(cause = externalCause ?: ExternalCancellationException())
    }

    override fun <ModelInit> updateModel(init: ModelInit) {
        model?.loadModel(init)
    }


    fun addClient (input: SharedFlow<ClientStateInterface>?) {
        if (input != null)
            clientListener?.listen(repositoryScope, input)
    }

    fun removeClient () {
        clientListener?.destroy()
    }

    protected open inner class ClientListener : AClientStateListener {
        constructor () : super()

        constructor (scope: CoroutineScope, clientFlow: SharedFlow<ClientStateInterface>) : super(scope, clientFlow)

        override suspend fun onLiveEvaluationReady() {
            triggerReadyState()
        }

        override suspend fun onLiveEvaluationStart(state: ClientState.Start<*>) {
            try {
                val typedState = state as ClientState.Start<I>
                if (mEvaluationFlowState.replayCache.last() == LiveEvaluationState.Ready(true)) {
                    liveEvaluationJob = jobEvaluation(typedState.input.buffer(1), typedState.settings)
                }

                if (evaluationStateProducer.isLast(LiveEvaluationState.Ready(true))) {
                    liveEvaluationJob = jobEvaluation(typedState.input.buffer(1), typedState.settings)
                }
            } catch (e : Throwable) {
                mEvaluationFlowState.emit(LiveEvaluationState.OldEnd(e, null))
                evaluationStateProducer.emitErrorEnd(e)
            }
        }

        override fun onLiveEvaluationStop(state: ClientState.Stop) {
            onStopLiveEvaluation(state.cause)
        }

        override fun onLiveEvaluationUpdateSettings(state: ClientState.UpdateSettings) {
            model?.updateThreshold(state.settings.modelThreshold)
            collectionOfWindows.updateSettings(state.settings)
        }
    }

    protected open inner class ModelListener : GenericListener<Boolean> {
        constructor () : super()

        constructor (scope: CoroutineScope, modelFlow: SharedFlow<Boolean>, mode: IGenericMode) :
                super(scope, modelFlow, mode)

        override suspend fun collectStates (state: Boolean) {
            super.collectStates(state)
            triggerReadyState()
        }
    }

    protected open inner class LiveEvaluationProducer :
        AProducer<LiveEvaluationStateInterface>(1, 5),
        ILiveEvaluationProducer<LiveEvaluationStateInterface>
    {

        override suspend fun emitReady(isReady: Boolean) {
            emit(LiveEvaluationState.Ready(isReady))
        }

        override suspend fun emitStart() {
            emit(LiveEvaluationState.Start)
        }

        override suspend fun emitLoading() {
            emit(
                LiveEvaluationState.Loading (
                    collectionOfWindows.totEvaluationsDone, collectionOfWindows.lastResult
                )
            )
        }

        override suspend fun emitErrorEnd(cause: Throwable) {
            emit(LiveEvaluationState.End(cause, null))
        }

        override suspend fun emitSuccessEnd() {
            emit(LiveEvaluationState.End(null, collectionOfWindows.getFinalResults()))
        }

        override fun tryEmitReady(isReady: Boolean): Boolean {
            return tryEmit(LiveEvaluationState.Ready(isReady))
        }

        override fun initialize () {
            producerIsInitialized.complete(null)
        }
    }
}