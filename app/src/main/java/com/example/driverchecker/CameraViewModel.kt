package com.example.driverchecker

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.*
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.repositories.ImageDetectionFactoryRepository
import com.example.driverchecker.machinelearning.helpers.ImageDetectionUtils
import com.example.driverchecker.media.MediaRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

data class StaticMedia (val path : String?, val isVideo: Boolean = false)

class CameraViewModel (private var imageDetectionRepository: ImageDetectionFactoryRepository? = null): ViewModel() {
    private val mediaRepository : MediaRepository = MediaRepository()
//    private var collectResultJob: Job?

    init {
        viewModelScope.launch(Dispatchers.Default) {
            analysisState?.collect { state ->
                when (state) {
                    is LiveEvaluationState.Ready -> {
                        clearPartialResults()
                        _onPartialResultsChanged.postValue(array.size)
                        _lastResult.postValue(null)
                        _isEvaluating.postValue(false)
                        _liveIsEnabled.postValue(state.isReady)
                        Log.d("LiveEvaluationState", "READY: ${state.isReady} with index ${_onPartialResultsChanged.value} but array.size is ${array.size}")
                    }
                    is LiveClassificationState.Start -> {
                        // add the partialResult to the resultsArray
                        _lastResult.postValue(null)
                        _isEvaluating.postValue(true)
                        _liveIsEnabled.postValue(true)
                        Log.d("LiveEvaluationState", "START: ${_onPartialResultsChanged.value} initialIndex and max classes: ${state.maxClassesPerGroup}")
                    }
                    is LiveEvaluationState.Loading<ImageDetectionArrayListOutput<String>> -> {
                        // add the partialResult to the resultsArray
                        if (!state.partialResult.isNullOrEmpty()) {
                            insertPartialResult(state.partialResult)
                            _onPartialResultsChanged.postValue(array.size)
                            _lastResult.postValue(state.partialResult)
                            Log.d("LiveEvaluationState", "LOADING: ${state.partialResult} for the ${_onPartialResultsChanged.value} time")
                        }
                    }
                    is LiveEvaluationState.End<ImageDetectionArrayListOutput<String>> -> {
                        // update the UI with the text of the class
                        // save to the database the result with bulk of 10 and video
                        _isEvaluating.postValue(false)
                        _liveIsEnabled.postValue(false)
                        Log.d("LiveEvaluationState", "END: ${state.result} for the ${_onPartialResultsChanged.value} time")
                    }
                    else -> {}
                }
            }
        }
    }

    val result: LiveData<ImageDetectionArrayListOutput<String>?>
        get() = _path.switchMap { media ->
            liveData {
                when {
                    media?.path == null -> emit (null)
//                    !media.isVideo -> emit(imageDetectionRepository?.instantClassification(media.path))
                    media.isVideo -> {
                        mediaRepository.extractVideo(media.path)
                        emit(imageDetectionRepository?.continuousClassification(mediaRepository.video!!.asFlow().map { bitmap -> ImageDetectionBaseInput(
                            bitmap
                        ) }, viewModelScope))
                    }
                }
            }
        }


    private val _lastResult: MutableLiveData<ImageDetectionArrayListOutput<String>?> = MutableLiveData(null)
    val lastResult: LiveData<ImageDetectionArrayListOutput<String>?>
        get() = _lastResult

    val analysisState: SharedFlow<LiveEvaluationStateInterface<ImageDetectionArrayListOutput<String>>>?
        get() = imageDetectionRepository?.analysisProgressState

    private val _imageUri: MutableLiveData<Uri?> = MutableLiveData(null)
    val imageUri: LiveData<Uri?>
        get() = _imageUri

    private val _isRecording: MutableLiveData<Boolean> = MutableLiveData(false)
    val isRecording: LiveData<Boolean>
        get() = _isRecording

    private val _isEnabled: MutableLiveData<Boolean> = MutableLiveData(true)
    val isEnabled: LiveData<Boolean>
    get() = _isEnabled


    private val _isEvaluating: MutableLiveData<Boolean> = MutableLiveData(false)
    val isEvaluating: LiveData<Boolean>
        get() = _isEvaluating

    private val _liveIsEnabled: MutableLiveData<Boolean> = MutableLiveData(false)
    val liveIsEnabled: LiveData<Boolean>
        get() = _liveIsEnabled

    private val _path: MutableLiveData<StaticMedia> = MutableLiveData(null)
    val path: LiveData<String?>
        get() = _path.switchMap { media ->
            liveData {
                emit(media.path)
            }
        }

    private val _pathVideo: MutableLiveData<String?> = MutableLiveData(null)
    val pathVideo: LiveData<String?>
        get() = _pathVideo

    private val _liveImages: MutableSharedFlow<ImageDetectionBaseInput> = MutableSharedFlow (
        replay = 0,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    val liveImages: SharedFlow<ImageDetectionBaseInput>
            get() = _liveImages.asSharedFlow()

    suspend fun produceImage (image: ImageProxy) {
        viewModelScope.launch {
            val bitmap: Bitmap = ImageDetectionUtils.imageProxyToBitmap(image)
            _liveImages.emit(ImageDetectionBaseInput(bitmap))
            image.close()
        }
    }



    private val _onPartialResultsChanged: MutableLiveData<Int> = MutableLiveData(-1)
    val onPartialResultsChanged: LiveData<Int>
        get () = _onPartialResultsChanged

    protected val array = ArrayList<ImageDetectionArrayListOutput<String>>()
    val list: List<ImageDetectionArrayListOutput<String>>
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

    protected fun insertPartialResult (partialResult: ImageDetectionArrayListOutput<String>) {
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

    fun updateImageUri (uri: Uri?) {
        _imageUri.value = uri
    }

    fun updatePath (path: String?) {
        _path.value = StaticMedia(path, false)
    }

    fun updatePathVideo (path: String?) {
        _path.value = StaticMedia(path, true)
    }

//    fun loadLocalModel (path: String) {
//        imageDetectionRepository?.updateLocalModel(path)
//    }

    fun recordVideo (record: Boolean) {
        _isRecording.value = record
    }

    fun enableVideo (enable: Boolean) {
        _isEnabled.value = enable
    }

    fun evaluateLive (record: Boolean) {
        _isEvaluating.value = record
    }

    fun enableLive (enable: Boolean) {
        _liveIsEnabled.value = enable
    }

//    fun setUrlModel (url: String) {
//        imageDetectionRepository?.updateRemoteModel(url)
//    }

    fun updateLiveClassification () {
        runBlocking(Dispatchers.Default) {
            when (_isEvaluating.value) {
                false -> {
                    imageDetectionRepository?.onStartLiveClassification(liveImages, viewModelScope)
                }
                true -> {
                    imageDetectionRepository?.onStopLiveClassification()
                }
                else -> {}
            }
        }
    }
}

data class Prediction (val classes: List<Int>, val superClass: Int, val bitmap: Bitmap)

class CameraViewModelFactory(private val repository: ImageDetectionFactoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CameraViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class $modelClass")
    }
}