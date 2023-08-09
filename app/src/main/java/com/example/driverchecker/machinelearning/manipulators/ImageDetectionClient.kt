package com.example.driverchecker.machinelearning.manipulators

import androidx.lifecycle.LiveData
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.listeners.ClassificationListener

class ImageDetectionClient : AClassificationClient<IImageDetectionData, IImageDetectionResultOld<String>, IImageDetectionOutputOld<String>, String>() {
    override val evaluationListener: ClassificationListener<String> = EvaluationImageDetectionListener()

    // FUNCTIONS

    override fun getOutput () : IImageDetectionOutputOld<String> {
        return ImageDetectionOutputOld(evaluatedItemsArray, "Driver", 6.0f)
    }

    override val output: LiveData<IImageDetectionOutputOld<String>?>
        get() = mOutput

    // handling the add of a partial result to the main array
    override fun insertPartialResult (partialResult: IImageDetectionResultOld<String>) {
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
            if (!(state.partialResult as IImageDetectionResultOld<String>?)?.listItems.isNullOrEmpty()) super.onLiveEvaluationLoading(state)
        }

        override fun onLiveClassificationEnd (state: LiveClassificationState.End<String>) {
            mOutput.postValue(ImageDetectionOutputOld(evaluatedItemsArray, state.finalResult!!.supergroup, state.finalResult.confidence))
            super.onLiveClassificationEnd(state)
        }
    }
}