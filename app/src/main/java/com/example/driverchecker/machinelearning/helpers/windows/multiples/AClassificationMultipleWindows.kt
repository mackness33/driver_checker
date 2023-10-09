package com.example.driverchecker.machinelearning.helpers.windows.multiples

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.singles.IClassificationSingleWindow
import kotlinx.coroutines.CoroutineScope

abstract class AClassificationMultipleWindows<E : IClassificationOutputStats<G>, G, W : IClassificationSingleWindow<E, G>, S : IClassificationSingleWindowSettings<G>> (scope: CoroutineScope) :
    AMachineLearningMultipleWindows<E, W, S> (scope),
    IClassificationMultipleWindows<E, G> {
    /* CLASSIFICATION */
    override var groups: Set<G> = emptySet()
        protected set

    /* DATA */
    override fun getAdditionalMetrics(): List<IGroupMetrics<G>?> {
        return selectedWindows.map { it.getAdditionalMetrics() }
    }

    override fun getData(): Map<IWindowBasicData, IGroupMetrics<G>?> {
        val listOfData = selectedWindows.map { it.getData() }
        return listOfData.toMap()
    }


    override fun updateGroups(newGroups: Set<G>) {
        groups = newGroups

        availableWindows.forEach { it.value.updateGroups(newGroups) }
    }
}
