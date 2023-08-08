package com.example.driverchecker.data

import androidx.lifecycle.*
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.listeners.MachineLearningListener
import com.example.driverchecker.machinelearning.manipulators.IMachineLearningClient
import com.example.driverchecker.machinelearning.repositories.IMachineLearningFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

abstract class BaseViewModel<Data, Result : WithConfidence, O : WithConfidence> (private var machineLearningRepository: IMachineLearningFactory<Data, Result, O>? = null): ViewModel(){
    // SHARED FLOWS

    // producer flow of the data in input of mlRepository
    protected val mLiveInput: MutableSharedFlow<Data> = MutableSharedFlow (
        replay = 0,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    val liveInput: SharedFlow<Data>
        get() = mLiveInput.asSharedFlow()

    // progress flow of the evaluation by the mlRepository
    val analysisState: SharedFlow<LiveEvaluationStateInterface>?
        get() = machineLearningRepository?.evaluationFlowState


    // LISTENERS

    protected open val evaluationListener: MachineLearningListener = EvaluationListener()

    // CLIENTS

    protected abstract val evaluationClient: IMachineLearningClient<Data, Result, O>

    // LIVE DATA

    // last result evaluated by the mlRepo
    val lastResult: LiveData<Result?>
        get() = evaluationClient.lastResult

    // bool to check if the mlRepo is evaluating
    protected val mIsEvaluating: MutableLiveData<Boolean> = MutableLiveData(false)
    val isEvaluating: LiveData<Boolean>
        get() = mIsEvaluating

    // bool to check if the button to start/stop the evaluation of mlRepo is enable
    protected val mIsEnabled: MutableLiveData<Boolean> = MutableLiveData(true)
    val isEnabled: LiveData<Boolean>
        get() = mIsEnabled

    // the index of the partialResult
    val partialResultEvent: LiveData<PartialEvaluationStateInterface>
        get () = evaluationClient.partialResultEvent

    // array of evaluated items by the mlRepo
    val evaluatedItemsList: List<Result>
        get() = evaluationClient.currentResultsList

//    open val finalOutput: LiveData<IMachineLearningOutput<Data, Result>>
//        get() = evaluationClient.getOutput()


    // FUNCTIONS

    // enabling the button to start/stop the evaluation of the ml
    fun enable (enable: Boolean) {
        mIsEnabled.value = enable
    }

    // start/stop the evaluation of the ml
    fun evaluate (record: Boolean) {
        mIsEvaluating.value = record
    }

    // update of the live classification of the mlRepo
    fun updateLiveClassification () {
        runBlocking(Dispatchers.Default) {
            when (mIsEvaluating.value) {
                false -> {
                    machineLearningRepository?.onStartLiveEvaluation(liveInput, viewModelScope)
                }
                true -> {
                    machineLearningRepository?.onStopLiveEvaluation()
                }
                else -> {}
            }
        }
    }

    // INNER CLASSES
    protected open inner class EvaluationListener : MachineLearningListener {
        override fun listen (scope: CoroutineScope, evaluationFlow: SharedFlow<LiveEvaluationStateInterface>?) {
            scope.launch(Dispatchers.Default) {
                evaluationFlow?.collect {state -> evaluationListener.collectLiveEvaluations(state)}
            }
        }

        override fun onLiveEvaluationReady(state: LiveEvaluationState.Ready) {
            mIsEvaluating.postValue(false)
            mIsEnabled.postValue(state.isReady)
        }

        override fun onLiveEvaluationStart() {
            // add the partialResult to the resultsArray
            mIsEvaluating.postValue(true)
            mIsEnabled.postValue(true)
        }

        override fun onLiveEvaluationEnd(state: LiveEvaluationState.End) {
            // update the UI with the text of the class
            // save to the database the result with bulk of 10 and video
            mIsEvaluating.postValue(false)
            mIsEnabled.postValue(false)
        }

        override fun onLiveEvaluationLoading(state: LiveEvaluationState.Loading) {}
    }
}