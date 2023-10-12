package com.example.driverchecker.machinelearning.helpers.windows.multiples

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.factories.IClassificationWindowFactory
import com.example.driverchecker.machinelearning.helpers.windows.singles.IClassificationSingleWindow
import kotlinx.coroutines.CoroutineScope

abstract class AClassificationMultipleWindows<E : IClassificationOutputStats<G>, G, W : IClassificationSingleWindow<E, G>, S : IClassificationSingleWindowSettings<G>> (scope: CoroutineScope) :
    AMachineLearningMultipleWindows<E, W, S> (scope),
    IClassificationMultipleWindows<E, G> {
    abstract override val factory: IClassificationWindowFactory<E, S, W, G>

    /* CLASSIFICATION */
    override var groups: Set<G> = emptySet()
        protected set

    /* DATA */
    override fun getAdditionalMetrics(): List<IGroupMetrics<G>?> {
        return currentWindows.values.map { it.getAdditionalMetrics() }
    }

    override fun getData(): Map<IWindowBasicData, IGroupMetrics<G>?> {
        val listOfData = currentWindows.values.map { it.getData() }
        return listOfData.toMap()
    }


    override fun updateGroups(newGroups: Set<G>) {
        groups = newGroups

//        availableWindows.forEach { it.value.updateGroups(newGroups) }
        currentWindows.forEach { (_, window) -> window.updateGroups(newGroups) }
    }
}
