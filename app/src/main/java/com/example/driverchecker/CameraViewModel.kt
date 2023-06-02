package com.example.driverchecker

import android.graphics.Bitmap
import android.net.Uri
import androidx.camera.core.ImageProxy
import androidx.lifecycle.*
import com.example.driverchecker.machinelearning.imagedetection.ImageDetectionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch

data class StaticMedia (val path : String?, val isVideo: Boolean)

class CameraViewModel (private var imageDetectionRepository: ImageDetectionRepository? = null): ViewModel() {
    private val mediaRepository : MediaRepository = MediaRepository()

    init {
        if (imageDetectionRepository == null)
            imageDetectionRepository = ImageDetectionRepository()
    }

    val result: LiveData<String>
        get() = _path.switchMap { media ->
            liveData {
                when {
                    media.path == null -> emit ("Image not found")
                    !media.isVideo -> emit(imageDetectionRepository?.instantClassification(media.path)?.result.toString())
                    media.isVideo -> {
                        mediaRepository.extractVideo(media.path)
                        val res = imageDetectionRepository?.continuousClassification(mediaRepository.video!!.asFlow(), viewModelScope)?.result.toString()
                        emit(res)
                    }
                }
            }
        }

    private val _imageUri: MutableLiveData<Uri?> = MutableLiveData(null)
    val imageUri: LiveData<Uri?>
        get() = _imageUri

    private val _isRecording: MutableLiveData<Boolean> = MutableLiveData(false)
    val isRecording: LiveData<Boolean>
        get() = _isRecording

    private val _isEnabled: MutableLiveData<Boolean> = MutableLiveData(true)
    val isEnabled: LiveData<Boolean>
    get() = _isEnabled

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

    private val _liveImages: MutableSharedFlow<ImageProxy> = MutableSharedFlow (
        replay = 0,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.SUSPEND
    )
    val liveImages: SharedFlow<ImageProxy>
            get() = _liveImages.asSharedFlow()

    suspend fun produceImage (image: ImageProxy) {
        viewModelScope.launch {
            _liveImages.emit(image)
        }
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

    fun setUrlModel (url: String) {
        imageDetectionRepository?.updateRemoteModel(url)
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