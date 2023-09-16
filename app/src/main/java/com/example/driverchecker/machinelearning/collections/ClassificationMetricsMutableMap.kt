package com.example.driverchecker.machinelearning.collections

import com.example.driverchecker.machinelearning.data.IClassificationOutputStats
import com.example.driverchecker.machinelearning.data.IClassificationWithMetrics
import com.example.driverchecker.utils.*

open class ClassificationMetricsMutableMap<S> : ClassificationMetricsMap<S> {
    protected val mMetrics: MutableMap<S, AtomicObservableData<Triple<Int, Int, Int>>> = mutableMapOf()
    override val liveMetrics: Map<S, ObservableData<Triple<Int, Int, Int>>>
        get() = mMetrics
    override val metrics: Map<S, Triple<Int, Int, Int>>
        get() = mMetrics.mapValues { entry -> entry.value.value }

    override fun initialize(keys: Set<S>) {
        mMetrics.putAll(keys.associateWith { LockableData(Triple(0, 0,0)) })
    }

    override fun replace (element: IClassificationOutputStats<S>) {
        element.groups.forEach { entry ->
            mMetrics[entry.key]?.postValue(Triple(1, entry.value.size, sumAllObjectsFound(entry.value)))
        }
    }

    override fun add (element: IClassificationOutputStats<S>) {
        element.groups.forEach { entry ->
            mMetrics[entry.key]?.postValue(tripleSum(mMetrics[entry.key]?.value, Triple(1, entry.value.size, sumAllObjectsFound(entry.value))))
        }
    }

    override fun subtract (element: IClassificationOutputStats<S>) {
        element.groups.forEach { entry ->
            mMetrics[entry.key]?.postValue(
                tripleSubtract(mMetrics[entry.key]?.value, Triple(1, entry.value.size, sumAllObjectsFound(entry.value)))
            )
        }
    }

    override fun remove (keys: Set<S>) {
        keys.forEach { mMetrics.remove(it) }
    }

    override fun clear () {
        mMetrics.clear()
    }

    private fun sumAllObjectsFound (setOfClassifications: Set<IClassificationWithMetrics<S>>) : Int {
        return setOfClassifications.fold(0) { sum, next ->
            sum + next.objectsFound
        }
    }

    protected fun tripleSum (first: Triple<Int, Int, Int>?, second: Triple<Int, Int, Int>?) : Triple<Int, Int, Int> {
        return Triple(
            (first?.first ?: 0) + (second?.first ?: 0),
            (first?.second ?: 0) + (second?.second ?: 0),
            (first?.third ?: 0) + (second?.third ?: 0)
        )
    }

    protected fun tripleSubtract (first: Triple<Int, Int, Int>?, second: Triple<Int, Int, Int>?) : Triple<Int, Int, Int> {
        return Triple(
            if (first?.first == null) 0 else first.first - (second?.first ?: 0),
            if (first?.second == null) 0 else first.second - (second?.second ?: 0),
            if (first?.third == null) 0 else first.third - (second?.third ?: 0)
        )
    }
}

