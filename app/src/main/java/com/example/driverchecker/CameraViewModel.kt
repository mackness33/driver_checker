package com.example.driverchecker

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class CameraViewModel : ViewModel() {

    private val _result: MutableLiveData<String> = MutableLiveData("This is the first value")
    val result: LiveData<String>
        get() = _result

    fun updateResult (path: String) {
        _result.value = path
    }

    companion object {
        fun newInstance(): ResultFragment {
            return ResultFragment()
        }
    }
}