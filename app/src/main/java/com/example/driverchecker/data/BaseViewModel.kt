package com.example.driverchecker.data

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.*
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.repositories.ImageDetectionFactoryRepository
import com.example.driverchecker.machinelearning.helpers.ImageDetectionUtils
import com.example.driverchecker.machinelearning.repositories.IMachineLearningFactory
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

abstract class BaseViewModel<Data, Result : WithConfidence> (protected var machineLearningRepository: IMachineLearningFactory<Data, Result>? = null): ViewModel() {

    init {
        listenToLiveClassification ()
    }

    val analysisState: SharedFlow<LiveEvaluationStateInterface<Result>>?
        get() = machineLearningRepository?.analysisProgressState

    protected val _lastResult: MutableLiveData<Result?> = MutableLiveData(null)
    val lastResult: LiveData<Result?>
        get() = _lastResult

    protected val _isEvaluating: MutableLiveData<Boolean> = MutableLiveData(false)
    val isEvaluating: LiveData<Boolean>
        get() = _isEvaluating

    protected val _isEnabled: MutableLiveData<Boolean> = MutableLiveData(true)
    val isEnabled: LiveData<Boolean>
        get() = _isEnabled

    protected val _liveData: MutableSharedFlow<Data> = MutableSharedFlow (
        replay = 0,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    val liveData: SharedFlow<Data>
        get() = _liveData.asSharedFlow()

    protected val _onPartialResultsChanged: MutableLiveData<Int> = MutableLiveData(-1)
    val onPartialResultsChanged: LiveData<Int>
        get () = _onPartialResultsChanged

    protected val array = ArrayList<Result>()
    val list: List<Result>
        get() = array

    protected val arrayClassesPredictions = ArrayList<Pair<Int, List<Int>>>()
    val simpleListClassesPredictions: List<Pair<Int, List<Int>>>
        get() = arrayClassesPredictions

    val predictionsGroupByClasses: List<Pair<Int, List<Int>>>
        get() = arrayClassesPredictions

    protected fun listenToLiveClassification () {
        viewModelScope.launch(Dispatchers.Default) {
            analysisState?.collect { state ->
                when (state) {
                    is LiveEvaluationState.Ready -> {
                        clearPartialResults()
                        _onPartialResultsChanged.postValue(array.size)
                        _lastResult.postValue(null)
                        _isEvaluating.postValue(false)
                        _isEnabled.postValue(state.isReady)
                        Log.d("LiveEvaluationState", "READY: ${state.isReady} with index ${_onPartialResultsChanged.value} but array.size is ${array.size}")
                    }
                    is LiveClassificationState.Start -> {
                        // add the partialResult to the resultsArray
                        _lastResult.postValue(null)
                        _isEvaluating.postValue(true)
                        _isEnabled.postValue(true)
                        Log.d("LiveEvaluationState", "START: ${_onPartialResultsChanged.value} initialIndex and max classes: ${state.maxClassesPerGroup}")
                    }
                    is LiveEvaluationState.Loading<Result> -> {
                        // add the partialResult to the resultsArray
                        if (state.partialResult != null) {
                            insertPartialResult(state.partialResult)
                            _onPartialResultsChanged.postValue(array.size)
                            _lastResult.postValue(state.partialResult)
                            Log.d("LiveEvaluationState", "LOADING: ${state.partialResult} for the ${_onPartialResultsChanged.value} time")
                        }
                    }
                    is LiveEvaluationState.End<Result> -> {
                        // update the UI with the text of the class
                        // save to the database the result with bulk of 10 and video
                        _isEvaluating.postValue(false)
                        _isEnabled.postValue(false)
                        Log.d("LiveEvaluationState", "END: ${state.result} for the ${_onPartialResultsChanged.value} time")
                    }
                    else -> {}
                }
            }
        }
    }

    protected open fun insertPartialResult (partialResult: Result) {
        array.add(partialResult)
    }

    protected open fun clearPartialResults () {
        array.clear()
        arrayClassesPredictions.clear()
    }

    fun enable (enable: Boolean) {
        _isEnabled.value = enable
    }

    fun evaluate (record: Boolean) {
        _isEvaluating.value = record
    }

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