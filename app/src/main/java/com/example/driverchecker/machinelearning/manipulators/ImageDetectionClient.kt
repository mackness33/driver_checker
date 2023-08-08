package com.example.driverchecker.machinelearning.manipulators

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.listeners.ClassificationListener

class ImageDetectionClient : MachineLearningClient<IImageDetectionData, IImageDetectionResult<String>, IImageDetectionOutput<String>> (), IClassificationClient<IImageDetectionData, IImageDetectionResult<String>, String, IImageDetectionOutput<String>> {
    // LIVE DATA
    private val mPassengerInfo = MutableLiveData(Pair(0, 0))
    override val passengerInfo: LiveData<Pair<Int, Int>>
        get() = mPassengerInfo

    private val mDriverInfo = MutableLiveData(Pair(0, 0))
    override val driverInfo: LiveData<Pair<Int, Int>>
        get() = mDriverInfo

    // REFACTOR: move this array/function to the mlRepo
    private val arrayClassesPredictions = ArrayList<Pair<Int, List<Int>>>()
    override val simpleListClassesPredictions: List<Pair<Int, List<Int>>>
        get() = arrayClassesPredictions


    override val evaluationListener: ClassificationListener<String> = EvaluationClassificationListener()

    // FUNCTIONS

    override fun getOutput () : IClassificationOutput<IImageDetectionData, IImageDetectionResult<String>, String> {
        return ClassificationOutput(evaluatedItemsArray, "Driver", 6.0f)
    }

    override val output: LiveData<IClassificationOutput<IImageDetectionData, IImageDetectionResult<String>, String>?>
        get() = mOutput

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
            0 -> mPassengerInfo.postValue(Pair((mPassengerInfo.value?.first ?: 0) + classInfo.first, (mPassengerInfo.value?.second ?: 0) + classInfo.second.count()))
            1 -> mDriverInfo.postValue(Pair((mDriverInfo.value?.first ?: 0) + classInfo.first, (mDriverInfo.value?.second ?: 0) + classInfo.second.count()))
        }
    }

    // handling the clearing of the main array
    override fun clearPartialResults () {
        evaluatedItemsArray.clear()
        arrayClassesPredictions.clear()
        mPassengerInfo.postValue(Pair(0, 0))
        mDriverInfo.postValue(Pair(0, 0))
        mHasEnded.tryUpdate(false)
    }

    // INNER CLASSES
    private open inner class EvaluationClassificationListener :
        ClassificationListener<String>,
        EvaluationListener() {
        override fun onLiveEvaluationLoading (state: LiveEvaluationState.Loading) {
            // add the partialResult to the resultsArray
            if (!(state.partialResult as IImageDetectionResult<String>?)?.listItems.isNullOrEmpty()) super.onLiveEvaluationLoading(state)
        }

        override fun onLiveEvaluationStart() {}

        override fun onLiveClassificationStart(state: LiveClassificationState.Start) {
            super.onLiveEvaluationStart()
            Log.d("LiveClassificationState", "START: ${mPartialResultEvent.value} initialIndex")
        }

        override fun onLiveEvaluationEnd(state: LiveEvaluationState.End) {}

        override fun onLiveClassificationEnd (state: LiveClassificationState.End<String>) {
//            mOutput.value = ImageDetectionOutput(evaluatedItemsArray, state.finalResult!!.supergroup, state.finalResult.confidence)
            super.onLiveEvaluationEnd(LiveEvaluationState.End(state.exception, state.finalResult))
            Log.d("LiveClassificationState", "END: ${state.finalResult} for the ${mPartialResultEvent.value} time")
        }
    }
}