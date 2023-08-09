package com.example.driverchecker.machinelearning.manipulators

import androidx.lifecycle.LiveData
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.listeners.ClassificationListener

class ImageDetectionClient : AClassificationClient<IImageDetectionInput, IImageDetectionOutput<String>, IImageDetectionFinalResult<String>, String>() {
    override val evaluationListener: ClassificationListener<String> = EvaluationImageDetectionListener()

    // FUNCTIONS

    override fun getOutput () : IImageDetectionFinalResult<String> {
        return ImageDetectionFinalResult(6.0f, "Driver")
    }

    override val output: LiveData<IImageDetectionFinalResult<String>?>
        get() = mOutput

    // handling the add of a partial result to the main array
    override fun insertPartialResult (partialResult: IImageDetectionOutput<String>) {
        super.insertPartialResult(partialResult)

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
        arrayClassesPredictions.clear()
        mPassengerInfo.postValue(Pair(0, 0))
        mDriverInfo.postValue(Pair(0, 0))
        super.clearPartialResults()
    }

    // INNER CLASSES
    private inner class EvaluationImageDetectionListener :
        EvaluationClassificationListener() {
        override fun onLiveEvaluationLoading (state: LiveEvaluationState.Loading) {
            // add the partialResult to the resultsArray
            if (!(state.partialResult as IImageDetectionOutput<String>?)?.listItems.isNullOrEmpty()) super.onLiveEvaluationLoading(state)
        }

        override fun onLiveClassificationEnd (state: LiveClassificationState.End<String>) {
            mOutput.postValue(ImageDetectionFinalResult(state.finalResult!!.confidence, state.finalResult.supergroup))
            super.onLiveClassificationEnd(state)
        }
    }
}