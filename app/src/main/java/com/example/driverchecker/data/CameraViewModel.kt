package com.example.driverchecker.data

import android.graphics.Bitmap
import android.util.Log
import androidx.camera.core.ImageProxy
import androidx.lifecycle.*
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.repositories.ImageDetectionFactoryRepository
import com.example.driverchecker.machinelearning.helpers.ImageDetectionUtils
import com.example.driverchecker.machinelearning.helpers.listeners.ClassificationListener
import com.example.driverchecker.machinelearning.manipulators.IClassificationClient
import com.example.driverchecker.machinelearning.manipulators.IMachineLearningClient
import com.example.driverchecker.machinelearning.manipulators.ImageDetectionClient
import com.example.driverchecker.machinelearning.manipulators.MachineLearningClient
import com.example.driverchecker.utils.AtomicLiveData
import kotlinx.coroutines.*

class CameraViewModel (imageDetectionRepository: ImageDetectionFactoryRepository? = null) : BaseViewModel<IImageDetectionData, ImageDetectionArrayListOutput<String>>(imageDetectionRepository) {
    override val client: IClassificationClient<IImageDetectionData, ImageDetectionArrayListOutput<String>> = ImageDetectionClient(imageDetectionRepository)

    val showResults: LiveData<Boolean?>
        get() = client.hasEnded

    val passengerInfo: LiveData<Pair<Int, Int>>
        get() = client.passengerInfo

    val driverInfo: LiveData<Pair<Int, Int>>
        get() = client.driverInfo


    // REFACTOR: move this array/function to the mlRepo
    val simpleListClassesPredictions: List<Pair<Int, List<Int>>>
        get() = client.simpleListClassesPredictions


    override val evaluationListener: ClassificationListener<IImageDetectionData, ImageDetectionArrayListOutput<String>> = EvaluationClassificationListener()


    init {
        evaluationListener.listen(viewModelScope, analysisState)
        client.listen(viewModelScope, analysisState)
    }

    suspend fun produceImage (image: ImageProxy) {
        viewModelScope.launch {
            val bitmap: Bitmap = ImageDetectionUtils.imageProxyToBitmap(image)
            _liveData.emit(ImageDetectionBaseInput(bitmap))
            image.close()
        }
    }

    // INNER CLASSES
    private open inner class EvaluationClassificationListener :
        ClassificationListener<IImageDetectionData, ImageDetectionArrayListOutput<String>>,
        EvaluationListener() {
        override fun onLiveEvaluationStart() {}

        override fun onLiveClassificationStart(state: LiveClassificationState.Start) {
            super.onLiveEvaluationStart()
        }

    }
}