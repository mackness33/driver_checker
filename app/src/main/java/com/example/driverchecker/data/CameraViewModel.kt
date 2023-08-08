package com.example.driverchecker.data

import android.graphics.Bitmap
import androidx.camera.core.ImageProxy
import androidx.lifecycle.*
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.repositories.ImageDetectionFactoryRepository
import com.example.driverchecker.machinelearning.helpers.ImageDetectionUtils
import com.example.driverchecker.machinelearning.helpers.listeners.ClassificationListener
import com.example.driverchecker.machinelearning.manipulators.IClassificationClient
import com.example.driverchecker.machinelearning.manipulators.ImageDetectionClient
import kotlinx.coroutines.*

class CameraViewModel (imageDetectionRepository: ImageDetectionFactoryRepository? = null) : BaseViewModel<IImageDetectionData, IImageDetectionResult<String>>(imageDetectionRepository) {
    override val evaluationClient: IClassificationClient<IImageDetectionData, IImageDetectionResult<String>, String> = ImageDetectionClient()

    val showResults: LiveData<Boolean?>
        get() = evaluationClient.hasEnded

    val passengerInfo: LiveData<Pair<Int, Int>>
        get() = evaluationClient.passengerInfo

    val driverInfo: LiveData<Pair<Int, Int>>
        get() = evaluationClient.driverInfo


    // REFACTOR: move this array/function to the mlRepo
    val simpleListClassesPredictions: List<Pair<Int, List<Int>>>
        get() = evaluationClient.simpleListClassesPredictions


    override val evaluationListener: ClassificationListener<IImageDetectionData, IImageDetectionResult<String>, String> = EvaluationClassificationListener()


    init {
        evaluationListener.listen(viewModelScope, analysisState)
        evaluationClient.listen(viewModelScope, analysisState)
    }

    suspend fun produceImage (image: ImageProxy) {
        viewModelScope.launch {
            val bitmap: Bitmap = ImageDetectionUtils.imageProxyToBitmap(image)
            mLiveInput.emit(ImageDetectionBaseInput(bitmap))
            image.close()
        }
    }

    // INNER CLASSES
    private open inner class EvaluationClassificationListener :
        ClassificationListener<IImageDetectionData, IImageDetectionResult<String>, String>,
        EvaluationListener() {
        override fun onLiveEvaluationStart() {}

        override fun onLiveClassificationStart(state: LiveClassificationState.Start) {
            super.onLiveEvaluationStart()
        }

        override fun onLiveEvaluationEnd(state: LiveEvaluationState.End) {}

        override fun onLiveClassificationEnd(state: LiveClassificationState.End<String>) {
            super.onLiveEvaluationEnd(LiveEvaluationState.End(state.exception, state.finalResult))
        }
    }
}