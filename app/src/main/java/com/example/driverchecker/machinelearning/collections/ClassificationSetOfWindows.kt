package com.example.driverchecker.machinelearning.collections

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.IClassificationWindow

open class ClassificationSetOfWindows<E : IClassificationOutputStats<S>, W : IClassificationWindow<E, S>, S> :
    ClassificationWindowsMutableCollection<E, W, S>, MachineLearningSetOfWindows<E> () {
    override var groups: Set<S> = emptySet()
        protected set

    /*  WINDOWS  */


    /* DATA */
    override fun getMetrics(): List<IWindowBasicData> {
        return mWindows.map { it.getMetrics() }
    }

    override fun getAdditionalMetrics(): List<IGroupMetrics<S>?> {
        return mWindows.map { it.getAdditionalMetrics() }
    }

    override fun getData(): Map<IWindowBasicData, IGroupMetrics<S>?> {
        return mWindows.associate { it.getData() }
    }

    override fun getFinalResults(): IClassificationFinalResult<S> {
        val finalGroupScore = groups.associateWith { 0 }.toMutableMap()
        var finalGroupWindow: S
        mWindows.forEach { window ->
            finalGroupWindow = window.getFinalGroup()
            finalGroupScore[finalGroupWindow] = (finalGroupScore[finalGroupWindow] ?: 0) + 1
        }

        return ClassificationFinalResult(
            confidence,
            finalGroupScore.maxWith { o1, o2 -> o1.value.compareTo(o2.value) }.key,
            getData()
        )
    }


    override fun updateGroups(newGroups: Set<S>) {
        groups = newGroups

        mWindows.forEach { it.updateGroups(newGroups) }
    }

    // TODO("NEED TO BE UPDATE")  finalResults need to return another type of results
    override fun getOldFinalResults(): IMachineLearningFinalResultStats {
        return first().getOldFinalResults()
    }

    // TODO("NEED TO BE UPDATE") metrics need to return another type of metrics
    override fun getOldMetrics(): IWindowOldMetrics {
        return first().getOldMetrics()
    }

    // TODO("NEED TO BE UPDATE") also this one will require a different type of metrics
    override fun getOldFullMetrics(): Pair<IWindowOldMetrics, IAdditionalMetrics?> {
        return first().getOldFullMetrics()
    }
}
