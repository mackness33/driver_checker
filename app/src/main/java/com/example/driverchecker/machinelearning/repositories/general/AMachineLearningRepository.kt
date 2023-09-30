package com.example.driverchecker.machinelearning.repositories.general

import android.util.Log
import com.example.driverchecker.machinelearning.collections.MachineLearningWindowsMutableCollection
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.listeners.*
import com.example.driverchecker.machinelearning.helpers.producers.*
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
import kotlinx.coroutines.sync.Semaphore
import kotlin.time.ExperimentalTime

abstract class AMachineLearningRepository<I, O : IMachineLearningOutputStats, FR: IMachineLearningFinalResult> (override val repositoryScope: CoroutineScope) :
    IMachineLearningRepository<I, O, FR> {
    protected val producerIsInitialized = CompletableDeferred<Nothing?>()

    open val evaluationStateProducer: ILiveEvaluationProducer<LiveEvaluationStateInterface> = LiveEvaluationProducer()
    protected val readySemaphore: IReactiveSemaphore<String> = ReadySemaphore()

    // abstracted
    protected abstract val model: IMachineLearningModel<I, O>?
    protected abstract var clientListener: ClientStateListener?
    protected abstract var modelListener: IGenericListener<Boolean>?

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
        get() = evaluationStateProducer.sharedFlow

    open fun initialize (semaphores: Set<String>) {
        readySemaphore.initialize(semaphores)
        evaluationStateProducer.initialize()
        collectionOfWindows.initialize(availableSettings)
        collectionOfWindows.updateSettings(privateSettings)
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

    override suspend fun instantEvaluation(input: I): O? {
        var result: O? = null
        val job = repositoryScope.launch(Dispatchers.Default) { result = model?.processAndEvaluate(input) }
        job.join()

        return result
    }

    override suspend fun continuousEvaluation(input: Flow<I>, settings: IOldSettings): O? {
        jobEvaluation(input, settings).join()

        return collectionOfWindows.lastResult
    }

    protected open fun jobEvaluation (input: Flow<I>, newSettings: IOldSettings): Job {
        return repositoryScope.launch(Dispatchers.Default) {
            // check if the repo is ready to make evaluations
            if (evaluationStateProducer.isLast(LiveEvaluationState.Ready(true))) {
                evaluationStateProducer.emitStart()
                timer.markStart()
                /* DELETABLE */
                model?.updateThreshold(newSettings.modelThreshold)
                oldSettings = newSettings

                oldTimer.markStart()

                flowEvaluation(input, ::cancel)?.collect()
            } else {
                evaluationStateProducer.emitErrorEnd(Throwable("The stream is not ready yet"))
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
            Log.e("AMLClassification", "Just caught this: ${cause.message}", cause)
            evaluationStateProducer.emitErrorEnd(cause)
        } else {
            oldTimer.markEnd()
            evaluationStateProducer.emitSuccessEnd()
        }

        oldTimer.reset()
        oldSettings = null
        timer.reset()
        collectionOfWindows.clean()
    }

    protected open suspend fun onEachEvaluation (
        postProcessedResult: O,
        onConditionSatisfied: (CancellationException) -> Unit
    ) {
        Log.d("JobClassification", "finally finished")
        timer.markEnd()
        collectionOfWindows.next(postProcessedResult, timer.diff())
        timer.markStart()

        if (collectionOfWindows.hasAcceptedLast) {
            evaluationStateProducer.emitLoading()
        }

        if (collectionOfWindows.isSatisfied())
            onConditionSatisfied(CorrectCancellationException())
    }

    protected open suspend fun onErrorEvaluation (cause: Throwable) {
        Log.e("JobClassification", "Just caught this: ${cause.message}")
    }

    override fun onStartLiveEvaluation(input: SharedFlow<I>) {}

    override fun onStopLiveEvaluation(externalCause: CancellationException?) {
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
            producerIsInitialized.await()
            readySemaphore.update("client",true, triggerAction = true)
        }

        override suspend fun onLiveEvaluationStart(state: ClientState.Start<*>) {
            try {
                val typedState = state as ClientState.Start<I>
                readySemaphore.update("client",false, triggerAction = false)
                if (evaluationStateProducer.isLast(LiveEvaluationState.Ready(true))) {
                    liveEvaluationJob = jobEvaluation(typedState.input.buffer(1), typedState.settings)
                }
            } catch (e : Throwable) {
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

            producerIsInitialized.await()
            readySemaphore.update("model", state, triggerAction = true)
        }
    }

    protected open inner class ReadySemaphore() : AReactiveSemaphore<String>() {
        protected var lastAction : Boolean? = null

        override fun initialize (semaphores: Set<String>) {
            super.initialize(semaphores)
//            triggerReadyState()
            lastAction = false
        }

        override suspend fun action() {
            val result = readyMap.values.fold(true) { last, current -> last && current }
            if (lastAction != result) {
                liveEvaluationJob?.cancel(InternalCancellationException())
                evaluationStateProducer.emitReady(result)
            }
        }
    }

    protected open inner class LiveEvaluationProducer :
        AAtomicProducer<LiveEvaluationStateInterface>(1, 5),
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
            tryEmit(LiveEvaluationState.Ready(false))
            producerIsInitialized.complete(null)
        }
    }
}