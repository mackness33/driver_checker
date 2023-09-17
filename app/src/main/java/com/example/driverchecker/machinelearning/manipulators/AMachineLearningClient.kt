package com.example.driverchecker.machinelearning.manipulators

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.driverchecker.machinelearning.collections.MachineLearningItemMutableList
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.listeners.AMachineLearningListener
import com.example.driverchecker.machinelearning.helpers.listeners.MachineLearningListener
import com.example.driverchecker.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

abstract class AMachineLearningClient<I, O : IMachineLearningOutputStats, FR : IMachineLearningFinalResult> : IMachineLearningClient<I, O, FR> {

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
    protected val evaluatedItemsArray = MachineLearningItemMutableList<O>()
    override val currentResultsList: List<O>
        get() = evaluatedItemsArray

    override var lastResultsList: List<O> = emptyList()
        protected set

    protected open val evaluationListener: MachineLearningListener = EvaluationListener()

    override var settings: IOldSettings = OldSettings(4, 0.80f, 0.10f)
        protected set

    // producer flow of the data in input of mlRepository
    protected val mLiveInput: MutableSharedFlow<I> = MutableSharedFlow (
        replay = 0,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    override val liveInput: SharedFlow<I>
        get() = mLiveInput.asSharedFlow()


    // producer flow of the data in input of mlRepository
    protected val mClientState: MutableSharedFlow<ClientStateInterface> = MutableSharedFlow (
        replay = 0,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    override val clientState: SharedFlow<ClientStateInterface>
        get() = mClientState.asSharedFlow()


    override suspend fun ready () {
        mClientState.emit(ClientState.Ready)
    }

    override suspend fun start () {
        mClientState.emit(ClientState.Start(liveInput, settings))
    }

    override suspend fun stop (cause: ExternalCancellationException) {
        mClientState.emit(ClientState.Stop(cause))
    }

    override suspend fun updateSettings (newSettings: ISettings) {
        mClientState.emit(ClientState.UpdateSettings(newSettings))
    }

    override fun updateoldSettings (newSettings: IOldSettings) {
        settings = newSettings
    }


    // FUNCTIONS
    override suspend fun produceInput (input: I) {
        mLiveInput.emit(input)
    }

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
            Log.d("MachineLearningClient - EvaluationListener", "READY: ${state.isReady} with index ${mPartialResultEvent.value} but array.size is ${evaluatedItemsArray.size}")
        }

        override suspend fun onLiveEvaluationStart() {
            mLastResult.postValue(null)
            Log.d("MachineLearningClient - EvaluationListener", "START: ${mPartialResultEvent.value} initialIndex")
        }

        override suspend fun onLiveEvaluationLoading(state: LiveEvaluationState.Loading) {
            // add the partialResult to the resultsArray
            try {
                if (state.partialResult != null && mHasEnded.value == false) {
                    val partialResult: O = state.partialResult as O
                    evaluatedItemsArray.add(partialResult)
                    mPartialResultEvent.postValue(PartialEvaluationState.Insert(evaluatedItemsArray.size))
                    mLastResult.postValue(partialResult)
                }
            } catch (e : Throwable) {
                Log.e("MachineLearningClient - EvaluationListener", "Client couldn't load the partial result properly", e)
            } finally {
                Log.d("MachineLearningClient - EvaluationListener", "LOADING: ${state.partialResult} for the ${mPartialResultEvent.value} time")
            }
        }

        override suspend fun onLiveEvaluationEnd(state: LiveEvaluationState.End) {
            // update the UI with the text of the class
            // save to the database the result with bulk of 10 and video
            mHasEnded.update(state.finalResult != null)
            lastResultsList = evaluatedItemsArray.toMutableList()
            Log.d("MachineLearningClient - EvaluationListener", "END: ${state.finalResult} for the ${mPartialResultEvent.value} time")
        }
    }
}
