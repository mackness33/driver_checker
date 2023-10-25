package com.example.driverchecker.machinelearning.windows.singles

import com.example.driverchecker.machinelearning.collections.ClassificationMetricsMutableMap
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.windows.helpers.IWindowTag

abstract class AClassificationSingleWindow<E : IClassificationOutput<S>, S> (
    initialSettings: IClassificationSingleWindowSettings<S>,
    internalTag: IWindowTag,
    ) : AMachineLearningSingleWindow<E>(initialSettings, internalTag),
    IClassificationSingleWindow<E, S> {
    protected val mSupergroupCounter: MutableMap<S, Int> = initialSettings.groups.associateWith { 0 }.toMutableMap()
    override val supergroupCounter: Map<S, Int>
        get() = mSupergroupCounter

    protected val mGroupMetrics: IMutableGroupMetrics<E, S> = ClassificationMetricsMutableMap()
    override val groupMetrics: IGroupMetrics<S>
        get() = mGroupMetrics

    init {
        mGroupMetrics.initialize(initialSettings.groups)
    }

    override val group : S?
        get() = calculateGroup()

    private fun calculateGroup() : S? {
        return if (windowIsFull() && supergroupCounter.isNotEmpty())
            mSupergroupCounter.maxWith { o1, o2 -> o1.value.compareTo(o2.value) }.key
        else
            null
    }

    /* SINGLE */
    override fun preUpdate (element: E) : Boolean {
        if (element.stats.groups.isEmpty()) {
            return false
        }

        val groupsToIncrease: Set<S> = element.stats.groups.keys
        val groupsToDecrease: Set<S> = if (windowIsFull()) window.first().stats.groups.keys else emptySet()
        val allPossibleKeysToUpdate = groupsToDecrease.union(groupsToIncrease).intersect(mSupergroupCounter.keys)

        // for each key to update in the counter I add to the value of the element and sub the element that is going to be removed
        allPossibleKeysToUpdate.forEach { key ->
            val increase: Boolean = groupsToIncrease.contains(key)
            val decrease: Boolean = groupsToDecrease.contains(key)

            when {
                !increase.xor(decrease) -> {}
                increase -> mSupergroupCounter[key] = mSupergroupCounter[key]!! + 1
                decrease -> mSupergroupCounter[key] = mSupergroupCounter[key]!! - 1
            }
        }

        return super.preUpdate(element)
    }

    override fun update () {
        if (supergroupCounter.isNotEmpty()) {
            mGroupMetrics.add(window.last())
            confidence = supergroupCounter.values.max().toFloat() / window.size
        }
    }

    override suspend fun clean () {
        super.clean()
        mSupergroupCounter.replaceAll { _, _ -> 0 }
        // TODO: create the clean function
        mGroupMetrics.clear()
        mGroupMetrics.initialize(mSupergroupCounter.keys)
    }


    /* MACHINE LEARNING */
    override fun getData(): Pair<IWindowBasicData, IGroupMetrics<S>?> {
        return getMetrics() to getAdditionalMetrics()
    }

    override fun getAdditionalMetrics(): IGroupMetrics<S>? {
        return mGroupMetrics.asImmutable()
    }

    /* CLASSIFICATION */
    override fun updateGroups(newGroups: Set<S>) {
        mSupergroupCounter.clear()
        mSupergroupCounter.putAll(newGroups.associateWith { 0 })
        mGroupMetrics.initialize(newGroups)
    }
}