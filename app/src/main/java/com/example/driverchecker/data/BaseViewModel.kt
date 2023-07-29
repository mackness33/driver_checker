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

    private val _lastResult: MutableLiveData<Result?> = MutableLiveData(null)
    val lastResult: LiveData<Result?>
        get() = _lastResult

    private val _isEvaluating: MutableLiveData<Boolean> = MutableLiveData(false)
    val isEvaluating: LiveData<Boolean>
        get() = _isEvaluating

    private val _isEnabled: MutableLiveData<Boolean> = MutableLiveData(true)
    val isEnabled: LiveData<Boolean>
        get() = _isEnabled

    private val _liveData: MutableSharedFlow<Data> = MutableSharedFlow (
        replay = 0,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    val liveData: SharedFlow<Data>
        get() = _liveData.asSharedFlow()

    private val _onPartialResultsChanged: MutableLiveData<Int> = MutableLiveData(-1)
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

    protected val _passengerInfo = MutableLiveData(Pair(0, 0))
    val passengerInfo: LiveData<Pair<Int, Int>>
        get() = _passengerInfo

    protected val _driverInfo = MutableLiveData(Pair(0, 0))
    val driverInfo: LiveData<Pair<Int, Int>>
        get() = _driverInfo

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

    protected fun insertPartialResult (partialResult: Result) {
        val classInfo: Pair<Int, List<Int>> = Pair(
            1,
            partialResult
                .distinctBy { predictions -> predictions.result.classIndex }
                .map { prediction -> prediction.result.classIndex}
        )

        array.add(partialResult)
        arrayClassesPredictions.add(classInfo)
        when (classInfo.first) {
            0 -> _passengerInfo.postValue(Pair((_passengerInfo.value?.first ?: 0) + classInfo.first, (_passengerInfo.value?.second ?: 0) + classInfo.second.count()))
            1 -> _driverInfo.postValue(Pair((_driverInfo.value?.first ?: 0) + classInfo.first, (_driverInfo.value?.second ?: 0) + classInfo.second.count()))
        }
    }

    protected fun clearPartialResults () {
        array.clear()
        arrayClassesPredictions.clear()
        _passengerInfo.postValue(Pair(0, 0))
        _driverInfo.postValue(Pair(0, 0))
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

class BaseViewModelFactory(private val repository: ImageDetectionFactoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BaseViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CameraViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class $modelClass")
    }
}