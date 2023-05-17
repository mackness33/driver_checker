package com.example.driverchecker

import android.net.Uri
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

class CameraViewModel : ViewModel() {

    private val _result: MutableLiveData<String> = MutableLiveData("This is the first value")
    val result: LiveData<String>
        get() = _result

    private val _imageUri: MutableLiveData<Uri?> = MutableLiveData(null)
    val imageUri: LiveData<Uri?>
        get() = _imageUri

    private val _path: MutableLiveData<String?> = MutableLiveData(null)
    val path: LiveData<String>
        get() = _path.switchMap { path ->
            liveData (Dispatchers.IO) {
                if (path == null)
                    emit ("Image not found")
                else
                    emit(ImageRecognitionService.makePredictionOfUri(path, true))
            }
        }

    fun updateResult (path: String) {
        _result.value = path
    }

    fun updateImageUri (uri: Uri?) {
        _imageUri.value = uri
    }

    fun updatePath (path: String?) {
        _path.value = path
    }

    companion object {
        fun newInstance(): ResultFragment {
            return ResultFragment()
        }
    }
}