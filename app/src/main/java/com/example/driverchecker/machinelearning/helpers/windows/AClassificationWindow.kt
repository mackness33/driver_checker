package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.collections.ClassificationMetricsMutableMap
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.utils.ISettings
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

@OptIn(ExperimentalTime::class)
abstract class AClassificationWindow<E : IClassificationOutputStats<S>, S> constructor(
    initialSize: Int = 3,
    initialThreshold: Float = 0.15f,
    newStart: TimeSource.Monotonic.ValueTimeMark? = null,
    supergroups: Set<S>
) : AMachineLearningWindow<E>(initialSize, initialThreshold, newStart), IClassificationWindow<E, S> {

    protected val mSupergroupCounter: MutableMap<S, Int> = supergroups.associateWith { 0 }.toMutableMap()
    override val supergroupCounter: Map<S, Int>
        get() = mSupergroupCounter

    protected val mGroupMetrics: IMutableGroupMetrics<S> = ClassificationMetricsMutableMap()
    override val groupMetrics: IGroupMetrics<S>
        get() = mGroupMetrics

    override fun initialize(
        settings: ISettings, newStart: TimeSource.Monotonic.ValueTimeMark?, supergroups: Set<S>
    ) {
        initialize(settings, newStart)
        mGroupMetrics.initialize(supergroups)
    }

    override fun preUpdate (element: E) : Boolean{
        if (element.groups.isEmpty()) {
            return false
        }

        val valueToDelete: E? = if (window.size < size) null else window.first()
        val allPossibleKeysToUpdate = (valueToDelete?.groups?.keys ?: emptySet()).union(element.groups.keys).intersect(mSupergroupCounter.keys)

        // for each key to update in the counter I add to the value of the element and sub the element that is going to be removed
        allPossibleKeysToUpdate.forEach { key ->
            val isInsertedElGroupPresent = if (element.groups[key] == null) 0 else 1
            val isRemovedElGroupPresent = if (valueToDelete?.groups?.get(key) == null) 0 else 1
            mSupergroupCounter[key] = (mSupergroupCounter[key] ?: 0) + isInsertedElGroupPresent - isRemovedElGroupPresent
        }

        return super.preUpdate(element)
    }

    override fun update () {
        if (window.size == 0) {
            confidence = 0.0f
            mSupergroupCounter.putAll(mSupergroupCounter.keys.associateWith { 0 })
        }

        confidence = supergroupCounter.values.max().toFloat() / window.size
        mGroupMetrics.add(window.last())
    }

    override fun clean () {
        super.clean()
        mSupergroupCounter.putAll(mSupergroupCounter.keys.associateWith { 0 })
        mGroupMetrics.clear()
    }

    override fun getFullMetrics() : Pair<IWindowMetrics, IGroupMetrics<S>> {
        return Pair(getMetrics(), groupMetrics)
    }
}