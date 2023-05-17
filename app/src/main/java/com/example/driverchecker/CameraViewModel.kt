package com.example.driverchecker

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {

    private val _result: MutableLiveData<String> = MutableLiveData("This is the first value")
    val result: LiveData<String>
        get() = _result

    private val _imageUri: MutableLiveData<Uri?> = MutableLiveData(null)
    val imageUri: LiveData<Uri?>
        get() = _imageUri

    fun updateResult (path: String) {
        _result.value = path
    }

    fun updateImageUri (uri: Uri?) {
        _imageUri.value = uri
    }

    companion object {
        fun newInstance(): ResultFragment {
            return ResultFragment()
        }
    }
}