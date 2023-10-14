package com.example.driverchecker.machinelearning.manipulators

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.IEvaluationsMap
import com.example.driverchecker.machinelearning.helpers.IMutableEvaluationsMap
import com.example.driverchecker.machinelearning.helpers.NotNullEvaluationsMap
import com.example.driverchecker.machinelearning.helpers.listeners.AMachineLearningListener
import com.example.driverchecker.machinelearning.helpers.listeners.MachineLearningListener
import com.example.driverchecker.machinelearning.helpers.producers.AAtomicProducer
import com.example.driverchecker.machinelearning.helpers.producers.AReactiveSemaphore
import com.example.driverchecker.machinelearning.helpers.producers.IClientStateProducer
import com.example.driverchecker.machinelearning.helpers.producers.IModelStateProducer
import com.example.driverchecker.utils.*
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.Semaphore

abstract class AMachineLearningClient<I : WithIndex, O : IMachineLearningOutput, FR : IMachineLearningFinalResult> : IMachineLearningClient<I, O, FR> {
    // 1. get the input and save it into a queue
    // 2. get output and remove from the queue.
        // IF the output is useful build the evaluation item and add it to the evaluation list
        // ELSE remove the input from the queue

    // PARTIALS is build by Input U Output specified from the generics.
    // Partials map is gonna be a Input -> Output
//    protected val inputQueue: Queue<IMachineLearningInput<I>> = LinkedList()
    protected val mEvaluationsMap: IMutableEvaluationsMap<WithIndex, I, O> = NotNullEvaluationsMap()
    val evaluationsMap: IEvaluationsMap<WithIndex, I, O> = mEvaluationsMap

    // LIVE DATA
    protected val mHasEnded: AtomicObservableData<Boolean> = LockableData(false)
    override val hasEnded: LiveData<Boolean>
        get() = mHasEnded.liveData

    // last result evaluated by the mlRepo
    protected val mLastResult: MutableLiveData<O?> = MutableLiveData(null)
    override val lastResult: LiveData<O?>
        get() = mLastResult

    // the index of the partialResult
    protected val mPartialResultEvent: MutableLiveData<PartialEvaluationStateInterface> = MutableLiveData(PartialEvaluationState.Init)
    override val partialResultEvent: LiveData<PartialEvaluationStateInterface>
        get () = mPartialResultEvent


    protected open val mFinalResult: MutableObservableData<FR?> = StatefulData(null)
    override val finalResult: ObservableData<FR?>
        get() = mFinalResult

    override val currentState: ObservableData<LiveEvaluationStateInterface?>
        get() = evaluationListener.currentState

    // VARIABLES
    // array of evaluated items by the mlRepo
    protected val evaluatedItemsArray: MutableList<O> = mutableListOf()
    override val currentResultsList: List<O?>
        get() = evaluationsMap.outputs

    override var lastResultsList: List<O> = emptyList()
        protected set

    protected open val evaluationListener: MachineLearningListener = EvaluationListener()

    // TODO: to be delete
    override var settings: IOldSettings = OldSettings(4, 0.80f, 0.10f)
        protected set

    // producer flow of the data in input of mlRepository
    protected val mLiveInput: MutableSharedFlow<I> = MutableSharedFlow (
        replay = 0,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    protected val mLiveInputProduce: InputProducer = InputProducer()
    override val liveInput: SharedFlow<I>
        get() = mLiveInputProduce.sharedFlow
//        get() = mLiveInput.asSharedFlow()


    // producer flow of the data in input of mlRepository
    protected val mClientState: MutableSharedFlow<ClientStateInterface> = MutableSharedFlow (
        replay = 0,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    protected val mClientStateProducer = ClientStateProducer()
    override val clientState: SharedFlow<ClientStateInterface>
        get() = mClientStateProducer.sharedFlow
//        get() = mClientState.asSharedFlow()




//    override suspend fun ready () {
//        mClientState.emit(ClientState.Ready)
//    }
    override suspend fun ready () = mClientStateProducer.emitReady()

//    override suspend fun start () {
//        mClientState.emit(ClientState.Start(liveInput, settings))
//    }
    override suspend fun start() = mClientStateProducer.emitStart()

//    override suspend fun stop (cause: ExternalCancellationException) {
//        mClientState.emit(ClientState.Stop(cause))
//    }
    override suspend fun stop (cause: ExternalCancellationException) = mClientStateProducer.emitStop(cause)

    override suspend fun updateSettings (newSettings: ISettingsOld) {
        mClientState.emit(ClientState.UpdateSettings(newSettings))
    }

    override fun updateoldSettings (newSettings: IOldSettings) {
        settings = newSettings
    }


    // FUNCTIONS
//    override suspend fun produceInput (input: I) {
//        mLiveInput.emit(input)
//        mEvaluationsMap.offerInput(input)
//    }
    override suspend fun produceInput(input: I) = mLiveInputProduce.produceInput(input)

    override fun listen (scope: CoroutineScope, evaluationFlow: SharedFlow<LiveEvaluationStateInterface>?) {
        evaluationListener.listen(scope, evaluationFlow)
    }

    // INNER CLASSES
    protected open inner class EvaluationListener : AMachineLearningListener {

        constructor () : super()

        constructor (scope: CoroutineScope, evaluationFlow: SharedFlow<LiveEvaluationStateInterface>) : super(scope, evaluationFlow)

        override suspend fun onLiveEvaluationReady(state: LiveEvaluationState.Ready) {
            evaluatedItemsArray.clear()
            mHasEnded.update(false)
            mPartialResultEvent.postValue(PartialEvaluationState.Clear)
            mLastResult.postValue(null)
            mEvaluationsMap.clear()
            Log.d("MachineLearningClient - EvaluationListener", "READY: ${state.isReady} with index ${mPartialResultEvent.value} but array.size is ${evaluatedItemsArray.size}")
        }

        override suspend fun onLiveEvaluationStart() {
            mLastResult.postValue(null)
            mLiveInputProduce.unlock()
            Log.d("MachineLearningClient - EvaluationListener", "START: ${mPartialResultEvent.value} initialIndex")
        }

        override suspend fun onLiveEvaluationLoading(state: LiveEvaluationState.Loading) {
            // add the partialResult to the resultsArray
            try {
                if (state.partialResult != null && !mHasEnded.value) {
                    state.partialResult as O
//                    evaluatedItemsArray.add(state.partialResult)
                    evaluatedItemsArray.add(state.partialResult)
                    mPartialResultEvent.postValue(PartialEvaluationState.Insert(evaluatedItemsArray.size))
                    mLastResult.postValue(state.partialResult)
                    mEvaluationsMap.submitOutput(state.partialResult)
                }
            } catch (e : Throwable) {
                Log.e("MachineLearningClient - EvaluationListener", "Client couldn't load the partial result properly", e)
            } finally {
                Log.d("MachineLearningClient - EvaluationListener", "LOADING: ${state.partialResult} for the ${mPartialResultEvent.value} time")
            }
        }

        override suspend fun onLiveEvaluationEnd(state: LiveEvaluationState.End) {
            // update the UI with the text of the class
            lastResultsList = evaluatedItemsArray.toMutableList()
            mHasEnded.update(state.finalResult != null)
            mLiveInputProduce.lock()
            Log.d("MachineLearningClient - EvaluationListener", "END: ${state.finalResult} for the ${mPartialResultEvent.value} time")
        }
    }

    protected open inner class ClientStateProducer
        : AAtomicProducer<ClientStateInterface>(0, 0, BufferOverflow.SUSPEND),
        IClientStateProducer<ClientStateInterface> {
        override fun initialize() {}
        override suspend fun emitReady() = emit(ClientState.Ready)

        override suspend fun emitStart() = emit(ClientState.Start(liveInput, settings))

        override suspend fun emitStop(cause: ExternalCancellationException) = emit(ClientState.Stop(cause))
    }

    protected open inner class InputProducer
        : AAtomicProducer<I>(0, 0, BufferOverflow.SUSPEND) {
        protected var evaluationHasStarted = CompletableDeferred<Nothing?>()
        private var isFirst: Boolean = false

        fun unlock() {
            evaluationHasStarted.complete(null)
        }

        fun lock() = runBlocking {
            evaluationHasStarted.cancelAndJoin()
            evaluationHasStarted = CompletableDeferred()
            isFirst = false
        }

        suspend fun produceInput (input: I) {
            evaluationHasStarted.await()
            if (!isFirst) {
                emit(input)
                mEvaluationsMap.offerInput(input)
            } else {
                isFirst = true
            }
        }

        override fun initialize() {
            lock()
        }
    }

}
