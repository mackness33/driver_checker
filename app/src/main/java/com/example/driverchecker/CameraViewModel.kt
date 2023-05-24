package com.example.driverchecker

import android.graphics.Bitmap
import android.net.Uri
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers

class CameraViewModel : ViewModel() {
    private val imageDetectionService: ImageDetectionService = ImageDetectionService()

    private val _frame: MutableLiveData<Bitmap?> = MutableLiveData(null)
    val frame: LiveData<String?>
        get() = _frame.switchMap { bitmap ->
            liveData (Dispatchers.IO) {
                if (bitmap == null)
                    emit ("Image not found")
                else{
                    val res = imageDetectionService.analyzeData(bitmap, false)
                    emit (res)
                }
//                    emit(imageDetectionService.analyzeData(bitmap, false))
            }
        }

    val result: LiveData<String>
        get() = _path.switchMap { path ->
            liveData (Dispatchers.IO) {
                if (path == null)
                    emit ("Image not found")
                else
                    emit(imageDetectionService.analyzeImagePath(path, false))
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

    fun nextFrame (bitmap: Bitmap?) {
        _frame.postValue(bitmap)
    }

    fun loadModel (path: String) {
        imageDetectionService.loadModel(path)
    }

    fun setUrlModel (url: String) {
        imageDetectionService.setUrlModel(url)
    }
}