package com.example.driverchecker.machinelearning.collections

import com.example.driverchecker.machinelearning.data.*
import java.util.*

open class MutableMachineLearningOutput<E : IMachineLearningItem> (collection: Collection<E>? = null)
    : List<E>, IMutableOutput<E> {

    protected val mItems: MutableList<E> = collection?.toMutableList() ?: mutableListOf()


    /* MACHINE LEARNING OUTPUT */
    override val items: List<E> = mItems
    override val stats: IMachineLearningOutputStats
        get() = MachineLearningStats(calculateConfidence())
    override val metrics: IMetrics? = null

    protected open fun calculateConfidence () : Float {
        if (this.size == 0) {
            return 0.0f
        }
        return fold(0.0f) { acc, next -> acc + next.confidence } / this.size
    }


    /* MUTABLE OUTPUT */
    override fun push(item: E) : Boolean = mItems.add(item)

    override fun getImmutable() : IMachineLearningOutput<E> = MachineLearningOutput(this)


    /*  LIST  */
    override val size: Int
        get() = mItems.size

    override fun contains(element: E): Boolean = mItems.contains(element)
    override fun containsAll(elements: Collection<E>): Boolean = mItems.containsAll(elements)
    override fun get(index: Int): E = mItems[index]
    override fun indexOf(element: E): Int = mItems.indexOf(element)
    override fun isEmpty(): Boolean = mItems.isEmpty()
    override fun iterator(): MutableIterator<E> = mItems.iterator()
    override fun lastIndexOf(element: E): Int = mItems.lastIndexOf(element)
    override fun listIterator(): MutableListIterator<E> = mItems.listIterator()
    override fun listIterator(index: Int): MutableListIterator<E> = mItems.listIterator(index)
    override fun subList(fromIndex: Int, toIndex: Int): MutableList<E> = mItems.subList(fromIndex, toIndex)
}
