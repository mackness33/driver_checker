package com.example.driverchecker.data

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import androidx.lifecycle.*
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.repositories.ImageDetectionFactoryRepository
import com.example.driverchecker.machinelearning.helpers.ImageDetectionUtils
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

class CameraViewModel (private var imageDetectionRepository: ImageDetectionFactoryRepository? = null) : BaseViewModel<IImageDetectionData, ImageDetectionArrayListOutput<String>>(imageDetectionRepository) {
    suspend fun produceImage (image: ImageProxy) {
        viewModelScope.launch {
            val bitmap: Bitmap = ImageDetectionUtils.imageProxyToBitmap(image)
            _liveData.emit(ImageDetectionBaseInput(bitmap))
            image.close()
        }
    }

    private val _passengerInfo = MutableLiveData(Pair(0, 0))
    val passengerInfo: LiveData<Pair<Int, Int>>
        get() = _passengerInfo

    private val _driverInfo = MutableLiveData(Pair(0, 0))
    val driverInfo: LiveData<Pair<Int, Int>>
        get() = _driverInfo

    override fun insertPartialResult (partialResult: ImageDetectionArrayListOutput<String>) {
        super.insertPartialResult(partialResult)

        val classInfo: Pair<Int, List<Int>> = Pair(
            1,
            partialResult
                .distinctBy { predictions -> predictions.result.classIndex }
                .map { prediction -> prediction.result.classIndex}
        )

        arrayClassesPredictions.add(classInfo)
        when (classInfo.first) {
            0 -> _passengerInfo.postValue(Pair((_passengerInfo.value?.first ?: 0) + classInfo.first, (_passengerInfo.value?.second ?: 0) + classInfo.second.count()))
            1 -> _driverInfo.postValue(Pair((_driverInfo.value?.first ?: 0) + classInfo.first, (_driverInfo.value?.second ?: 0) + classInfo.second.count()))
        }
    }

    override fun clearPartialResults () {
        super.clearPartialResults()
        _passengerInfo.postValue(Pair(0, 0))
        _driverInfo.postValue(Pair(0, 0))
    }
}

class CameraViewModelFactory(private val repository: ImageDetectionFactoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CameraViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class $modelClass")
    }
}