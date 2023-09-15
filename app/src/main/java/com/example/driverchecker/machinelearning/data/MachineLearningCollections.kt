package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.utils.MutableStateLiveData
import com.example.driverchecker.utils.StatefulLiveData
import com.example.driverchecker.utils.StateLiveData

interface MachineLearningList<E : IMachineLearningItem> : List<E>,  IMachineLearningOutputStats
interface ClassificationItemList<E : IClassificationItem<S>, S> : MachineLearningList<E>,  IClassificationOutputStats<S>

open class MachineLearningMutableList<E : IMachineLearningItem> : ArrayList<E>, MachineLearningList<E> {
    constructor(initialCapacity: Int) : super(initialCapacity)
    constructor(collection: Collection<E>) : super(collection)
    constructor() : super(10)

    final override var confidence: Float
        protected set

    init {
        confidence = initConfidence()
    }

    private fun initConfidence() : Float = calculateConfidence()

    protected open fun calculateConfidence () : Float {
        if (this.size == 0) {
            return 0.0f
        }
        return fold(0.0f) { acc, next -> acc + next.confidence } / this.size
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
            confidence = calculateConfidence()

        return result
    }

    override fun remove(element: E): Boolean {
        val result: Boolean = super.remove(element)

        if (result)
            confidence = calculateConfidence()

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
            confidence = calculateConfidence()

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
            confidence = calculateConfidence()

        return result
    }

    override fun removeAll(elements: Collection<E>): Boolean {
        val result: Boolean = super.removeAll(elements.toSet())

        if (result)
            confidence = calculateConfidence()

        return result
    }
    override fun retainAll(elements: Collection<E>): Boolean {
        val result: Boolean = super.retainAll(elements.toSet())

        if (result)
            confidence = calculateConfidence()

        return result
    }
    override fun clear(): Unit {
        super.clear()
        confidence = calculateConfidence()
    }

    // Positional Access Operations
    /**
     * Replaces the element at the specified position in this list with the specified element.
     *
     * @return the element previously at the specified position.
     */
    override operator fun set(index: Int, element: E): E {
        val result: E = super.set(index, element)
        confidence = calculateConfidence()

        return result
    }

    /**
     * Inserts an element into the list at the specified [index].
     */
    override fun add(index: Int, element: E): Unit {
        super.add(element)
        confidence = calculateConfidence()
    }

    /**
     * Removes an element at the specified [index] from the list.
     *
     * @return the element that has been removed.
     */
    override fun removeAt(index: Int): E {
        val result: E = super.removeAt(index)
        confidence = calculateConfidence()

        return result
    }
}

open class ClassificationItemMutableList<E : IClassificationItem<S>, S> :
    MachineLearningMutableList<E>, ClassificationItemList<E, S> {
    constructor(initialCapacity: Int) : super(initialCapacity)
    constructor(collection: Collection<E>) : super(collection)
    constructor() : super()

    protected val mutableGroups: MutableMap<S, MutableSet<IMutableClassificationWithMetrics<S>>> = mutableMapOf()
    final override val groups: Map<S, Set<IClassificationWithMetrics<S>>>
        get() = mutableGroups

    protected open fun putClassification (element: E) {
        when {
            !mutableGroups.containsKey(element.classification.supergroup) -> {
                mutableGroups[element.classification.supergroup] = mutableSetOf(MutableClassificationWithMetrics(element.classification))
            }
            mutableGroups[element.classification.supergroup]!!.find { classMetric ->
                classMetric.externalIndex == element.classification.externalIndex
            } == null -> {
                mutableGroups[element.classification.supergroup]!!.add(MutableClassificationWithMetrics(element.classification))
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

interface ClientMetricsMap<S> {
    val liveMetrics: Map<S, StateLiveData<Triple<Int, Int, Int>?>>
    val metrics: Map<S, Triple<Int, Int, Int>>

    fun initialize (keys: Set<S>)
    fun replace (element: IClassificationOutputStats<S>)
    fun add (element: IClassificationOutputStats<S>)
    fun subtract (element: IClassificationOutputStats<S>)
    fun remove (keys: Set<S>)
    fun clear ()
}

open class ClientMetricsMutableMap<S> : ClientMetricsMap <S> {
    protected val mMetrics: MutableMap<S, MutableStateLiveData<Triple<Int, Int, Int>?>> = mutableMapOf()
    override val liveMetrics: Map<S, StateLiveData<Triple<Int, Int, Int>?>>
        get() = mMetrics
    override val metrics: Map<S, Triple<Int, Int, Int>>
        get() = mMetrics.mapValues { entry -> entry.value.lastValue ?: Triple(0,0,0) }

    override fun initialize(keys: Set<S>) {
        mMetrics.putAll(keys.associateWith { StatefulLiveData(Triple(0, 0,0)) })
    }

    override fun replace (element: IClassificationOutputStats<S>) {
        element.groups.forEach { entry ->
            mMetrics[entry.key]?.postValue(Triple(1, entry.value.size, sumAllObjectsFound(entry.value)))
        }
    }

    override fun add (element: IClassificationOutputStats<S>) {
        element.groups.forEach { entry ->
            mMetrics[entry.key]?.postValue(tripleSum(mMetrics[entry.key]?.lastValue, Triple(1, entry.value.size, sumAllObjectsFound(entry.value))))
        }
    }

    override fun subtract (element: IClassificationOutputStats<S>) {
        element.groups.forEach { entry ->
            mMetrics[entry.key]?.postValue(
                tripleSubtract(mMetrics[entry.key]?.lastValue, Triple(1, entry.value.size, sumAllObjectsFound(entry.value)))
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

