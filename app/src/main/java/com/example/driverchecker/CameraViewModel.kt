package com.example.driverchecker

import android.graphics.Bitmap
import android.net.Uri
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.*
import com.example.driverchecker.machinelearning.data.ImageDetectionInput
import com.example.driverchecker.machinelearning.general.local.LiveEvaluationState
import com.example.driverchecker.machinelearning.general.local.LiveEvaluationStateInterface
import com.example.driverchecker.machinelearning.imagedetection.ImageDetectionArrayResult
import com.example.driverchecker.machinelearning.imagedetection.ImageDetectionRepository
import com.example.driverchecker.media.MediaRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

data class StaticMedia (val path : String?, val isVideo: Boolean = false)

class CameraViewModel (private var imageDetectionRepository: ImageDetectionRepository? = null): ViewModel() {
    private val mediaRepository : MediaRepository = MediaRepository()
//    private var collectResultJob: Job?

    init {
        if (imageDetectionRepository == null)
            imageDetectionRepository = ImageDetectionRepository()

        viewModelScope.launch(Dispatchers.Default) {
            analysisState?.collect { state ->
                when (state) {
                    is LiveEvaluationState.Ready -> {
                        clearPartialResults()
                        _onPartialResultsChanged.postValue(array.size)
                        _lastResult.postValue(null)
                        _isEvaluating.postValue(false)
                        _liveIsEnabled.postValue(state.isReady)
                        Log.d("LiveEvaluationState", "READY: ${_onPartialResultsChanged.value} but array.size is ${array.size}")
                    }
                    is LiveEvaluationState.Start -> {
                        // add the partialResult to the resultsArray
                        _lastResult.postValue(null)
                        _isEvaluating.postValue(true)
                        _liveIsEnabled.postValue(true)
                        Log.d("LiveEvaluationState", "START: ${_onPartialResultsChanged.value} initialIndex")

                    }
                    is LiveEvaluationState.Loading<ImageDetectionArrayResult> -> {
                        // add the partialResult to the resultsArray
                        if (state.partialResult != null) {
                            insertPartialResult(state.partialResult)
                            _onPartialResultsChanged.postValue(array.size)
                            _lastResult.postValue(state.partialResult)
                            Log.d("LiveEvaluationState", "LOADING: ${state.partialResult} for the ${_onPartialResultsChanged.value} time")
                        }
                    }
                    is LiveEvaluationState.End<ImageDetectionArrayResult> -> {
                        // update the UI with the text of the class
                        // save to the database the result with bulk of 10 and video
                        _isEvaluating.postValue(false)
                        _liveIsEnabled.postValue(false)
                        Log.d("LiveEvaluationState", "END: ${state.result} for the ${_onPartialResultsChanged.value} time")
                    }
                }
            }
        }
    }

    val result: LiveData<ImageDetectionArrayResult?>
        get() = _path.switchMap { media ->
            liveData {
                when {
                    media?.path == null -> emit (null)
                    !media.isVideo -> emit(imageDetectionRepository?.instantClassification(media.path))
                    media.isVideo -> {
                        mediaRepository.extractVideo(media.path)
                        emit(imageDetectionRepository?.continuousClassification(mediaRepository.video!!.asFlow().map { bitmap -> ImageDetectionInput(bitmap) }, viewModelScope))
                    }
                }
            }
        }


    private val _lastResult: MutableLiveData<ImageDetectionArrayResult?> = MutableLiveData(null)
    val lastResult: LiveData<ImageDetectionArrayResult?>
        get() = _lastResult

    val analysisState: SharedFlow<LiveEvaluationStateInterface<ImageDetectionArrayResult>>?
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

    private val _liveImages: MutableSharedFlow<ImageDetectionInput> = MutableSharedFlow (
        replay = 0,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    val liveImages: SharedFlow<ImageDetectionInput>
            get() = _liveImages.asSharedFlow()

    suspend fun produceImage (image: ImageProxy) {
        viewModelScope.launch {
            val bitmap: Bitmap? = imageDetectionRepository?.imageProxyToBitmap(image)
            if (bitmap != null) {
                _liveImages.emit(ImageDetectionInput(bitmap))
            }
            image.close()
        }
    }



    private val _onPartialResultsChanged: MutableLiveData<Int> = MutableLiveData(-1)
    val onPartialResultsChanged: LiveData<Int>
        get () = _onPartialResultsChanged

    protected val array = ArrayList<ImageDetectionArrayResult>()
    var list: List<ImageDetectionArrayResult> = array
        private set

    protected val arrayClassesPredictions = ArrayList<Pair<Int, List<Int>>>()
    var listClassesPredictions: List<Pair<Int, List<Int>>> = arrayClassesPredictions
        private set

    protected fun insertPartialResult (partialResult: ImageDetectionArrayResult) {
        array.add(partialResult)
        arrayClassesPredictions.add(
            Pair(
                1,
                partialResult
                    .distinctBy { predictions -> predictions.result.classIndex }
                    .map { prediction -> prediction.result.classIndex}
            )
        )
    }

    protected fun clearPartialResults () {
        array.clear()
        arrayClassesPredictions.clear()
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

    fun loadLocalModel (path: String) {
        imageDetectionRepository?.updateLocalModel(path)
    }

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

    fun setUrlModel (url: String) {
        imageDetectionRepository?.updateRemoteModel(url)
    }

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

fun <T> Array<T>.mapInPlace(transform: (T) -> T) {
    for (i in this.indices) {
        this[i] = transform(this[i])
    }
}

class CameraViewModelFactory(private val repository: ImageDetectionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CameraViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class $modelClass")
    }
}