package com.example.driverchecker.machinelearning.collections

import com.example.driverchecker.machinelearning.data.IClassificationItem
import com.example.driverchecker.machinelearning.data.IClassificationWithMetrics
import com.example.driverchecker.machinelearning.data.IMutableClassificationWithMetrics
import com.example.driverchecker.machinelearning.data.MutableClassificationWithMetrics

open class ClassificationItemMutableList<E : IClassificationItem<S>, S> :
    MachineLearningItemMutableList<E>, ClassificationItemList<E, S> {
    constructor(initialCapacity: Int) : super(initialCapacity)
    constructor(collection: Collection<E>) : super(collection)
    constructor() : super()

    protected val mutableGroups: MutableMap<S, MutableSet<IMutableClassificationWithMetrics<S>>> = mutableMapOf()
    final override val groups: Map<S, Set<IClassificationWithMetrics<S>>>
        get() = mutableGroups

    protected open fun putClassification (element: E) {
        when {
            !mutableGroups.containsKey(element.classification.supergroup) -> {
                mutableGroups[element.classification.supergroup] = mutableSetOf(
                    MutableClassificationWithMetrics(element.classification)
                )
            }
            mutableGroups[element.classification.supergroup]!!.find { classMetric ->
                classMetric.externalIndex == element.classification.externalIndex
            } == null -> {
                mutableGroups[element.classification.supergroup]!!.add(
                    MutableClassificationWithMetrics(element.classification)
                )
            }
            else -> {
                mutableGroups[element.classification.supergroup]!!.find { it.externalIndex == element.classification.externalIndex }?.inc()
            }
        }
    }

    protected open fun removeClassification (element: E) {
        if (mutableGroups[element.classification.supergroup]?.find { classMetric ->
                classMetric.externalIndex == element.classification.externalIndex
            } != null) {

            mutableGroups[element.classification.supergroup]!!.toList()[element.classification.externalIndex].dec()

            if (mutableGroups[element.classification.supergroup]!!.toList()[element.classification.externalIndex].objectsFound <= 0)
                mutableGroups[element.classification.supergroup]!!.remove(element.classification)

            if (mutableGroups[element.classification.supergroup].isNullOrEmpty())
                mutableGroups.remove(element.classification.supergroup)
        }
    }

    // Modification Operations
    /**
     * Adds the specified element to the end of this list.
     *
     * @return `true` because the list is always modified as the result of this operation.
     */
    override fun add(element: E): Boolean {
        val result: Boolean = super.add(element)

        if (result)
            putClassification(element)

        return result
    }

    override fun remove(element: E): Boolean {
        val result: Boolean = super.remove(element)

        if (result)
            removeClassification(element)

        return result
    }

    // Bulk Modification Operations
    /**
     * Adds all of the elements of the specified collection to the end of this list.
     *
     * The elements are appended in the order they appear in the [elements] collection.
     *
     * @return `true` if the list was changed as the result of the operation.
     */
    override fun addAll(elements: Collection<E>): Boolean {
        val result: Boolean = super.addAll(elements)

        if (result)
            elements.forEach { putClassification(it) }

        return result
    }

    /**
     * Inserts all of the elements of the specified collection [elements] into this list at the specified [index].
     *
     * @return `true` if the list was changed as the result of the operation.
     */
    override fun addAll(index: Int, elements: Collection<E>): Boolean {
        val result: Boolean = super.addAll(index, elements)

        if (result)
            elements.forEach { putClassification(it) }

        return result
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        val result: Boolean = super.removeAll(elements.toSet())

        if (result)
            elements.forEach { removeClassification(it) }

        return result
    }
    override fun retainAll(elements: Collection<E>): Boolean {
        val result: Boolean = super.retainAll(elements.toSet())

        if (result) {
            this.filter { !elements.contains(it) }.forEach { removeClassification(it) }
        }

        return result
    }

    override fun clear(): Unit {
        super.clear()
        mutableGroups.clear()
    }

    // Positional Access Operations
    /**
     * Replaces the element at the specified position in this list with the specified element.
     *
     * @return the element previously at the specified position.
     */
    override operator fun set(index: Int, element: E): E {
        val result: E = super.set(index, element)

        removeClassification(result)
        putClassification(element)

        return result
    }

    /**
     * Inserts an element into the list at the specified [index].
     */
    override fun add(index: Int, element: E) {
        super.add(element)
        putClassification(element)
    }

    /**
     * Removes an element at the specified [index] from the list.
     *
     * @return the element that has been removed.
     */
    override fun removeAt(index: Int): E {
        val result: E = super.removeAt(index)

        removeClassification(result)

        return result
    }
}
