package com.example.driverchecker.machinelearning.helpers.windows.multiples

import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.singles.IClassificationSingleWindow
import com.example.driverchecker.machinelearning.helpers.windows.singles.IMachineLearningSingleWindow
import com.example.driverchecker.machinelearning.helpers.windows.singles.ImageDetectionSingleWindow
import com.example.driverchecker.utils.DeferrableData
import com.example.driverchecker.utils.MutableCompletableData
import kotlinx.coroutines.CoroutineScope

abstract class ClassificationMultipleWindows<E : IClassificationOutputStats<S>, S, W : IClassificationSingleWindow<E, S>> (scope: CoroutineScope) :
    AMachineLearningMultipleWindows<E, W> (scope),
    IClassificationMultipleWindows<E, S> {
    /* CLASSIFICATION */
    override var groups: Set<S> = emptySet()
        protected set

    /* DATA */
    override fun getAdditionalMetrics(): List<IGroupMetrics<S>?> {
        return selectedWindows.map { it.getAdditionalMetrics() }
    }

    override fun getData(): Map<IWindowBasicData, IGroupMetrics<S>?> {
        val listOfData = selectedWindows.map { it.getData() }
        return listOfData.toMap()
    }


    override fun updateGroups(newGroups: Set<S>) {
        groups = newGroups

        availableWindows.forEach { it.value.updateGroups(newGroups) }
    }
}
