package com.example.driverchecker.machinelearning.windows.multiples

import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.windows.factories.IClassificationWindowFactory
import com.example.driverchecker.machinelearning.windows.factories.IClassificationWindowFactoryOld
import com.example.driverchecker.machinelearning.windows.singles.IClassificationSingleWindow
import kotlinx.coroutines.CoroutineScope

abstract class AClassificationMultipleWindows<E : IClassificationOutput<G>, G, W : IClassificationSingleWindow<E, G>, S : IOffsetSingleWindowSettings> (scope: CoroutineScope) :
    AMachineLearningMultipleWindows<E, W, S>(scope),
    IClassificationMultipleWindows<E, G> {
    abstract override val factory: IClassificationWindowFactory<E, S, W, G>

    /* CLASSIFICATION */
    final override var groups: Set<G> = emptySet()
        protected set
    protected val finalGroupsCounter: MutableMap<G, Int> = groups.associateWith { 0 }.toMutableMap()



    override fun onWindowSatisfied(window: W) {
        val finalWindowGroup = window.group
        if (finalWindowGroup != null && finalGroupsCounter.contains(finalWindowGroup))
            finalGroupsCounter[finalWindowGroup] = finalGroupsCounter[finalWindowGroup]!! + 1
        super.onWindowSatisfied(window)
    }

    /* DATA */
    override fun getAdditionalMetrics(): List<IGroupMetrics<G>?> {
        return currentWindows.values.map { it.getAdditionalMetrics() }
    }

    override fun getData(): Map<IWindowBasicData, IGroupMetrics<G>?> {
        val listOfData = currentWindows.values.map { it.getData() }
        return listOfData.toMap()
    }

    /*  WINDOWS  */
    override fun <M : IMultipleWindowSettings> update (newSettings: M) {
        super.update(newSettings)
        updateGroups(groups)
    }


    override fun updateGroups(newGroups: Set<G>) {
        groups = newGroups

        finalGroupsCounter.clear()
        finalGroupsCounter.putAll(groups.associateWith { 0 })

        currentWindows.forEach { (_, window) -> window.updateGroups(newGroups) }
    }

    override suspend fun clean() {
        super.clean()
        finalGroupsCounter.replaceAll { _, _ -> 0}
    }
}