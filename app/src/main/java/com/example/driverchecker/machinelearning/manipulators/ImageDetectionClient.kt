package com.example.driverchecker.machinelearning.manipulators

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.driverchecker.data.BaseViewModel
import com.example.driverchecker.machinelearning.data.IImageDetectionData
import com.example.driverchecker.machinelearning.data.ImageDetectionArrayListOutput
import com.example.driverchecker.machinelearning.data.LiveClassificationState
import com.example.driverchecker.machinelearning.data.LiveEvaluationState
import com.example.driverchecker.machinelearning.helpers.listeners.ClassificationListener
import com.example.driverchecker.machinelearning.repositories.ImageDetectionFactoryRepository
import com.example.driverchecker.utils.AtomicLiveData

class ImageDetectionClient (imageDetectionRepository: ImageDetectionFactoryRepository? = null) : MachineLearningClient<IImageDetectionData, ImageDetectionArrayListOutput<String>> (imageDetectionRepository), IClassificationClient<IImageDetectionData, ImageDetectionArrayListOutput<String>> {
    // LIVE DATA
    private val _passengerInfo = MutableLiveData(Pair(0, 0))
    override val passengerInfo: LiveData<Pair<Int, Int>>
        get() = _passengerInfo

    private val _driverInfo = MutableLiveData(Pair(0, 0))
    override val driverInfo: LiveData<Pair<Int, Int>>
        get() = _driverInfo

    // REFACTOR: move this array/function to the mlRepo
    private val arrayClassesPredictions = ArrayList<Pair<Int, List<Int>>>()
    override val simpleListClassesPredictions: List<Pair<Int, List<Int>>>
        get() = arrayClassesPredictions


    override val evaluationListener: ClassificationListener<IImageDetectionData, ImageDetectionArrayListOutput<String>> = EvaluationClassificationListener()


    // FUNCTIONS

    // handling the add of a partial result to the main array
    override fun insertPartialResult (partialResult: ImageDetectionArrayListOutput<String>) {
        evaluatedItemsArray.add(partialResult)

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

    // handling the clearing of the main array
    override fun clearPartialResults () {
        evaluatedItemsArray.clear()
        arrayClassesPredictions.clear()
        _passengerInfo.postValue(Pair(0, 0))
        _driverInfo.postValue(Pair(0, 0))
        _hasEnded.tryUpdate(false)
    }

    // INNER CLASSES
    private open inner class EvaluationClassificationListener :
        ClassificationListener<IImageDetectionData, ImageDetectionArrayListOutput<String>>,
        EvaluationListener() {
        override fun onLiveEvaluationEnd(state: LiveEvaluationState.End<ImageDetectionArrayListOutput<String>>) {
            _hasEnded.tryUpdate(state.result != null)
            super.onLiveEvaluationEnd(state)
        }

        override fun onLiveEvaluationLoading (state: LiveEvaluationState.Loading<ImageDetectionArrayListOutput<String>>) {
            // add the partialResult to the resultsArray
            if (!state.partialResult.isNullOrEmpty()) super.onLiveEvaluationLoading(state)
        }

        override fun onLiveEvaluationStart() {}

        override fun onLiveClassificationStart(state: LiveClassificationState.Start) {
            super.onLiveEvaluationStart()
            Log.d("LiveEvaluationState", "START: ${_partialResultEvent.value} initialIndex")
        }

    }
}