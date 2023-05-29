package com.example.driverchecker

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.*
import com.example.driverchecker.machinelearning.imagedetection.ImageDetectionRepository
import kotlinx.coroutines.Dispatchers

class CameraViewModel (): ViewModel() {
    private var imageDetectionRepository: ImageDetectionRepository? = null

    constructor(repository: ImageDetectionRepository) : this() {
        imageDetectionRepository = repository
    }

    private val _frame: MutableLiveData<Bitmap?> = MutableLiveData(null)
    val frame: LiveData<String?>
        get() = _frame.switchMap { bitmap ->
            liveData (Dispatchers.Default) {
                if (bitmap == null)
                    emit ("Image not found")
                else
                    emit(imageDetectionRepository?.instantClassification(bitmap)?.result)
            }
        }

    val result: LiveData<String>
        get() = _path.switchMap { path ->
            liveData (Dispatchers.Default) {
                if (path == null)
                    emit ("Image not found")
                else
                    emit(imageDetectionRepository?.instantClassification(path)?.result ?: "Image not found")
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

    private val _path: MutableLiveData<String?> = MutableLiveData(null)
    val path: LiveData<String?>
        get() = _path

    fun updateImageUri (uri: Uri?) {
        _imageUri.value = uri
    }

    fun updatePath (path: String?) {
        _path.value = path
    }

    fun nextFrame (bitmap: Bitmap?) {
        _frame.postValue(bitmap)
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

    fun setImageDetectionRepository (localUri: String?, remoteUri: String?) {
        imageDetectionRepository = ImageDetectionRepository.getInstance(localUri, remoteUri)
    }
}

class CameraViewModelFactory(private val repository: ImageDetectionRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ImageDetectionRepository::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CameraViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class $modelClass")
    }
}