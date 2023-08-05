package com.example.driverchecker.machinelearning.manipulators

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.listeners.ClassificationListener

class ImageDetectionClient : MachineLearningClient<IImageDetectionData, IImageDetectionResult<String>> (), IClassificationClient<IImageDetectionData, IImageDetectionResult<String>, String> {
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


    override val evaluationListener: ClassificationListener<IImageDetectionData, IImageDetectionResult<String>, String> = EvaluationClassificationListener()

    // FUNCTIONS

    override fun getOutput () : IClassificationOutput<IImageDetectionData, IImageDetectionResult<String>, String>? {
        return null
    }

    // handling the add of a partial result to the main array
    override fun insertPartialResult (partialResult: IImageDetectionResult<String>) {
        evaluatedItemsArray.add(partialResult)

        val classInfo: Pair<Int, List<Int>> = Pair(
            1,
            partialResult.listItems
                .distinctBy { predictions -> predictions.classIndex }
                .map { prediction -> prediction.classIndex}
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
        mHasEnded.tryUpdate(false)
    }

    // INNER CLASSES
    private open inner class EvaluationClassificationListener :
        ClassificationListener<IImageDetectionData, IImageDetectionResult<String>, String>,
        EvaluationListener() {
        override fun onLiveEvaluationLoading (state: LiveEvaluationState.Loading<IImageDetectionResult<String>>) {
            // add the partialResult to the resultsArray
            if (!state.partialResult?.listItems.isNullOrEmpty()) super.onLiveEvaluationLoading(state)
        }

        override fun onLiveEvaluationStart() {}

        override fun onLiveClassificationStart(state: LiveClassificationState.Start) {
            super.onLiveEvaluationStart()
            Log.d("LiveClassificationState", "START: ${mPartialResultEvent.value} initialIndex")
        }

        override fun onLiveEvaluationEnd(state: LiveEvaluationState.End) {}

        override fun onLiveClassificationEnd (state: LiveClassificationState.End<String>) {
            super.onLiveEvaluationEnd(LiveEvaluationState.End(state.exception, state.finalResult))
            Log.d("LiveClassificationState", "END: ${state.finalResult} for the ${mPartialResultEvent.value} time")
        }
    }
}