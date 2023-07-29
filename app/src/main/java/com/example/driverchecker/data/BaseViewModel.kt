package com.example.driverchecker.data

import android.util.Log
import androidx.lifecycle.*
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.repositories.IMachineLearningFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

abstract class BaseViewModel<Data, Result : WithConfidence> (protected var machineLearningRepository: IMachineLearningFactory<Data, Result>? = null): ViewModel() {
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

    // LIVE DATA

    // last result evaluated by the mlRepo
    protected val _lastResult: MutableLiveData<Result?> = MutableLiveData(null)
    val lastResult: LiveData<Result?>
        get() = _lastResult

    // bool to check if the mlRepo is evaluating
    protected val _isEvaluating: MutableLiveData<Boolean> = MutableLiveData(false)
    val isEvaluating: LiveData<Boolean>
        get() = _isEvaluating

    // bool to check if the button to start/stop the evaluation of mlRepo is enable
    protected val _isEnabled: MutableLiveData<Boolean> = MutableLiveData(true)
    val isEnabled: LiveData<Boolean>
        get() = _isEnabled

    // the index of the partialResult
    protected val _onPartialResultsChanged: MutableLiveData<Int> = MutableLiveData(-1)
    val onPartialResultsChanged: LiveData<Int>
        get () = _onPartialResultsChanged

    // array of evaluated items by the mlRepo
    protected val evaluatedItemsArray = ArrayList<Result>()
    val evaluatedItemsList: List<Result>
        get() = evaluatedItemsArray

    // REFACTOR: move this array/function to the mlRepo
    protected val arrayClassesPredictions = ArrayList<Pair<Int, List<Int>>>()
    val simpleListClassesPredictions: List<Pair<Int, List<Int>>>
        get() = arrayClassesPredictions

    // REFACTOR: move this array/function to the mlRepo
    val predictionsGroupByClasses: List<Pair<Int, List<Int>>>
        get() = arrayClassesPredictions

    // FUNCTION
    // listening of the live evaluation of the mlRepo
    protected open fun listenToLiveClassification() {
        viewModelScope.launch(Dispatchers.Default) {
            analysisState?.collect {state -> collectLiveClassification(state)}
        }
    }

    // collection of the live evaluation of the mlRepo
    protected open suspend fun collectLiveClassification (state: LiveEvaluationStateInterface<Result>) {
        when (state) {
            is LiveEvaluationState.Ready -> onLiveEvaluationReady(state)
            is LiveClassificationState.Start -> onLiveClassificationStart(state)
            is LiveEvaluationState.Loading<Result> -> onLiveEvaluationLoading(state)
            is LiveEvaluationState.End<Result> -> onLiveEvaluationEnd(state)
            else -> {}
        }
    }

    // handler of mlRepo in ready
    protected open fun onLiveEvaluationReady (state: LiveEvaluationState.Ready) {
        clearPartialResults()
        _onPartialResultsChanged.postValue(evaluatedItemsArray.size)
        _lastResult.postValue(null)
        _isEvaluating.postValue(false)
        _isEnabled.postValue(state.isReady)
        Log.d("LiveEvaluationState", "READY: ${state.isReady} with index ${_onPartialResultsChanged.value} but array.size is ${evaluatedItemsArray.size}")
    }

    // handler of mlRepo in start (as a classification)
    protected open fun onLiveClassificationStart (state: LiveClassificationState.Start) {
        // add the partialResult to the resultsArray
        _lastResult.postValue(null)
        _isEvaluating.postValue(true)
        _isEnabled.postValue(true)
        Log.d("LiveEvaluationState", "START: ${_onPartialResultsChanged.value} initialIndex and max classes: ${state.maxClassesPerGroup}")
    }

    // handler of mlRepo in loading
    protected open fun onLiveEvaluationLoading (state: LiveEvaluationState.Loading<Result>) {
        // add the partialResult to the resultsArray
        if (state.partialResult != null) {
            insertPartialResult(state.partialResult)
            _onPartialResultsChanged.postValue(evaluatedItemsArray.size)
            _lastResult.postValue(state.partialResult)
            Log.d("LiveEvaluationState", "LOADING: ${state.partialResult} for the ${_onPartialResultsChanged.value} time")
        }
    }

    // handler of mlRepo on end
    protected open fun onLiveEvaluationEnd (state: LiveEvaluationState.End<Result>) {
        // update the UI with the text of the class
        // save to the database the result with bulk of 10 and video
        _isEvaluating.postValue(false)
        _isEnabled.postValue(false)
        Log.d("LiveEvaluationState", "END: ${state.result} for the ${_onPartialResultsChanged.value} time")
    }

    // handling the add of a partial result to the main array
    protected open fun insertPartialResult (partialResult: Result) {
        evaluatedItemsArray.add(partialResult)
    }

    // handling the clearing of the main array
    protected open fun clearPartialResults () {
        evaluatedItemsArray.clear()
        arrayClassesPredictions.clear()
    }

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
}