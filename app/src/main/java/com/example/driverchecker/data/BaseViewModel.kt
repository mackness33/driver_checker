package com.example.driverchecker.data

import androidx.lifecycle.*
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.listeners.MachineLearningListener
import com.example.driverchecker.machinelearning.manipulators.IMachineLearningClient
import com.example.driverchecker.machinelearning.repositories.IMachineLearningFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

abstract class BaseViewModel<Data, Result : WithConfidence> (private var machineLearningRepository: IMachineLearningFactory<Data, Result>? = null): ViewModel(){
    // SHARED FLOWS

    // producer flow of the data in input of mlRepository
    protected val _liveData: MutableSharedFlow<Data> = MutableSharedFlow (
        replay = 0,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    val liveData: SharedFlow<Data>
        get() = _liveData.asSharedFlow()

    // progress flow of the evaluation by the mlRepository
    val analysisState: SharedFlow<LiveEvaluationStateInterface<Result>>?
        get() = machineLearningRepository?.analysisProgressState


    // LISTENERS

    protected open val evaluationListener: MachineLearningListener<Data, Result> = EvaluationListener()

    // CLIENTS

    protected abstract val client: IMachineLearningClient<Data, Result>

    // LIVE DATA

    // last result evaluated by the mlRepo
    val lastResult: LiveData<Result?>
        get() = client.lastResult

    // bool to check if the mlRepo is evaluating
    protected val _isEvaluating: MutableLiveData<Boolean> = MutableLiveData(false)
    val isEvaluating: LiveData<Boolean>
        get() = _isEvaluating

    // bool to check if the button to start/stop the evaluation of mlRepo is enable
    protected val _isEnabled: MutableLiveData<Boolean> = MutableLiveData(true)
    val isEnabled: LiveData<Boolean>
        get() = _isEnabled

    // the index of the partialResult
    val partialResultEvent: LiveData<PartialEvaluationStateInterface>
        get () = client.partialResultEvent

    // array of evaluated items by the mlRepo
    val evaluatedItemsList: List<Result>
        get() = client.currentResultsList



    // FUNCTIONS

    // enabling the button to start/stop the evaluation of the ml
    fun enable (enable: Boolean) {
        _isEnabled.value = enable
    }

    // start/stop the evaluation of the ml
    fun evaluate (record: Boolean) {
        _isEvaluating.value = record
    }

    // update of the live classification of the mlRepo
    fun updateLiveClassification () {
        runBlocking(Dispatchers.Default) {
            when (_isEvaluating.value) {
                false -> {
                    machineLearningRepository?.onStartLiveClassification(liveData, viewModelScope)
                }
                true -> {
                    machineLearningRepository?.onStopLiveClassification()
                }
                else -> {}
            }
        }
    }

    // INNER CLASSES
    protected open inner class EvaluationListener : MachineLearningListener<Data, Result> {
        override fun listen (scope: CoroutineScope, evaluationFlow: SharedFlow<LiveEvaluationStateInterface<Result>>?) {
            scope.launch(Dispatchers.Default) {
                evaluationFlow?.collect {state -> evaluationListener.collectLiveEvaluations(state)}
            }
        }

        override fun onLiveEvaluationReady(state: LiveEvaluationState.Ready) {
            _isEvaluating.postValue(false)
            _isEnabled.postValue(state.isReady)
        }

        override fun onLiveEvaluationStart() {
            // add the partialResult to the resultsArray
            _isEvaluating.postValue(true)
            _isEnabled.postValue(true)
        }

        override fun onLiveEvaluationEnd(state: LiveEvaluationState.End<Result>) {
            // update the UI with the text of the class
            // save to the database the result with bulk of 10 and video
            _isEvaluating.postValue(false)
            _isEnabled.postValue(false)
        }

        override fun onLiveEvaluationLoading(state: LiveEvaluationState.Loading<Result>) {}
    }
}