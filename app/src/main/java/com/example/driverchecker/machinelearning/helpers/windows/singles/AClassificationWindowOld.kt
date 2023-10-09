package com.example.driverchecker.machinelearning.helpers.windows.singles

import com.example.driverchecker.machinelearning.collections.ClassificationMetricsMutableMap
import com.example.driverchecker.machinelearning.data.*
import kotlin.time.ExperimentalTime
import kotlin.time.TimeSource

@OptIn(ExperimentalTime::class)
abstract class AClassificationWindowOld<E : IClassificationOutputStats<S>, S> constructor(
    initialSize: Int = 3,
    initialThreshold: Float = 0.15f,
    newStart: TimeSource.Monotonic.ValueTimeMark? = null,
    supergroups: Set<S>
) : AMachineLearningWindowOld<E>(initialSize, initialThreshold, newStart),
    IClassificationWindowOld<E, S> {

    protected val mSupergroupCounter: MutableMap<S, Int> = supergroups.associateWith { 0 }.toMutableMap()
    override val supergroupCounter: Map<S, Int>
        get() = mSupergroupCounter

    protected val mGroupMetrics: IMutableGroupMetrics<S> = ClassificationMetricsMutableMap()
    override val groupMetrics: IGroupMetrics<S>
        get() = mGroupMetrics

    override fun getFinalGroup(): S = mSupergroupCounter.maxWith { o1, o2 -> o1.value.compareTo(o2.value) }.key
    override fun initialize(

        settings: IOldSettings, newStart: TimeSource.Monotonic.ValueTimeMark?, supergroups: Set<S>
    ) {
        initialize(settings, newStart)
        mSupergroupCounter.putAll(mSupergroupCounter.keys.associateWith { 0 })
        mGroupMetrics.initialize(supergroups)
    }

    override fun preUpdate (element: E) : Boolean{
        if (element.groups.isEmpty() || element.groups.size > 1) {
            return false
        }

        val valueToDelete: E? = if (window.size < windowFrames) null else window.first()
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

        if (supergroupCounter.isEmpty()) {
            super.update()
        } else {
            mGroupMetrics.add(window.last())
            confidence = supergroupCounter.values.max().toFloat() / window.size
        }
    }

    override suspend fun clean () {
        super.clean()
        mSupergroupCounter.putAll(mSupergroupCounter.keys.associateWith { 0 })
        mGroupMetrics.clear()
        mGroupMetrics.initialize(mSupergroupCounter.keys)
    }

    override fun getData(): Pair<IWindowBasicData, IGroupMetrics<S>?> {
        return getMetrics() to getAdditionalMetrics()
    }

    override fun getAdditionalMetrics(): IGroupMetrics<S>? {
        return mGroupMetrics.copyMetrics()
    }

    override fun updateGroups(newGroups: Set<S>) {
        mSupergroupCounter.clear()
        mSupergroupCounter.putAll(newGroups.associateWith { 0 })
        mGroupMetrics.initialize(newGroups)
    }
}