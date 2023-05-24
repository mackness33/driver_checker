package com.example.driverchecker

import android.net.Uri
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers

class CameraViewModel : ViewModel() {
    private val imageDetectionService: ImageDetectionService = ImageDetectionService()

    val result: LiveData<String>
        get() = _path.switchMap { path ->
            liveData (Dispatchers.IO) {
                if (path == null)
                    emit ("Image not found")
                else
                    emit(imageDetectionService.analyzeData(path, false))
            }
        }

    private val _imageUri: MutableLiveData<Uri?> = MutableLiveData(null)
    val imageUri: LiveData<Uri?>
        get() = _imageUri

    private val _path: MutableLiveData<String?> = MutableLiveData(null)
    val path: LiveData<String?>
        get() = _path

    fun updateImageUri (uri: Uri?) {
        _imageUri.value = uri
    }

    fun updatePath (path: String?) {
        _path.value = path
    }

    fun loadModel (path: String) {
        imageDetectionService.loadModel(path)
    }

    fun setUrlModel (url: String) {
        imageDetectionService.setUrlModel(url)
    }
}