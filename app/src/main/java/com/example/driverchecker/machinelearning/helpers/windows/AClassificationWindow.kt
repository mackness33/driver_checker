package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.collections.ClassificationMetricsMutableMap
import com.example.driverchecker.machinelearning.data.*
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

@OptIn(ExperimentalTime::class)
abstract class AClassificationWindow<E : IClassificationOutputStats<S>, S> constructor(
    size: Int = 3,
    threshold: Float = 0.15f,
    newStart: TimeSource.Monotonic.ValueTimeMark? = null,
    supergroups: Set<S>
) : AMachineLearningWindow<E>(size, threshold, newStart), IClassificationWindow<E, S> {

    protected val mSupergroupCounter: MutableMap<S, Int> = supergroups.associateWith { 0 }.toMutableMap()
    override val supergroupCounter: Map<S, Int>
        get() = mSupergroupCounter

//    protected val mGroupMetrics: IMutableGroupMetrics<S> = ClassificationMetricsMutableMap()
//    override val groupMetrics: IGroupMetrics<S>
//        get() = mGroupMetrics

    override fun next (element: E) {
        if (element.groups.isEmpty()) {
            hasAcceptedLast = false
            return
        }

        val valueToDelete: E? = if (window.size < size) null else window.first()
        val allPossibleKeysToUpdate = (valueToDelete?.groups?.keys ?: emptySet()).union(element.groups.keys).intersect(mSupergroupCounter.keys)

        // for each key to update in the counter I add to the value of the element and sub the element that is going to be removed
        allPossibleKeysToUpdate.forEach { key ->
            val isInsertedElGroupPresent = if (element.groups[key] == null) 0 else 1
            val isRemovedElGroupPresent = if (valueToDelete?.groups?.get(key) == null) 0 else 1
            mSupergroupCounter[key] = (mSupergroupCounter[key] ?: 0) + isInsertedElGroupPresent - isRemovedElGroupPresent
        }

        super.next(element)
    }

    override fun update () {
        if (window.size == 0) {
            confidence = 0.0f
            mSupergroupCounter.putAll(mSupergroupCounter.keys.associateWith { 0 })
        }

        confidence = supergroupCounter.values.max().toFloat() / window.size
//        if (lastResult != null) mGroupMetrics.add(lastResult!!)
    }

    override fun clean () {
        super.clean()
        mSupergroupCounter.putAll(mSupergroupCounter.keys.associateWith { 0 })
    }

//    override fun getFullMetrics() : Pair<IWindowMetrics, IGroupMetrics<S>> {
//        return Pair(getMetrics(), groupMetrics)
//    }
}