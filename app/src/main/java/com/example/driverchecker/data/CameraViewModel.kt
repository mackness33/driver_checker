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

class CameraViewModel (imageDetectionRepository: ImageDetectionFactoryRepository? = null) : BaseViewModel<IImageDetectionInput, IImageDetectionOutput<String>, IImageDetectionFinalResult<String>>(imageDetectionRepository) {
    override val evaluationClient: IClassificationClient<IImageDetectionInput, IImageDetectionOutput<String>, IImageDetectionFinalResult<String>, String> = ImageDetectionClient()

    val showResults: LiveData<Boolean?>
        get() = evaluationClient.hasEnded

    val passengerInfo: LiveData<Pair<Int, Int>>
        get() = evaluationClient.passengerInfo

    val driverInfo: LiveData<Pair<Int, Int>>
        get() = evaluationClient.driverInfo


    // REFACTOR: move this array/function to the mlRepo
    val simpleListClassesPredictions: List<Pair<Int, List<Int>>>
        get() = evaluationClient.simpleListClassesPredictions


    override val evaluationListener: ClassificationListener<String> = EvaluationClassificationListener()


    init {
        evaluationListener.listen(viewModelScope, evaluationState)
        evaluationClient.listen(viewModelScope, evaluationState)
    }

    suspend fun produceImage (image: ImageProxy) {
        viewModelScope.launch {
            (evaluationClient as ImageDetectionClient).produceImage(image)
            image.close()
        }
    }

    // INNER CLASSES
    private open inner class EvaluationClassificationListener :
        ClassificationListener<String>,
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