package com.example.driverchecker.machinelearning.data

interface WithConfidence {
    val confidence: Float
}

interface MachineLearningResultList<Result> : List<Result>,  WithConfidence

open class MachineLearningResultArrayList<Result : WithConfidence> : ArrayList<Result>, MachineLearningResultList<Result> {
    constructor(initialCapacity: Int) : super(initialCapacity)
    constructor(collection: Collection<Result>) : super(collection)
    constructor() : super(10)

    override var confidence: Float
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
    override fun add(element: Result): Boolean {
        val result: Boolean = super.add(element)
        confidence = calculateConfidence()

        return result
    }

    override fun remove(element: Result): Boolean {
        val result: Boolean = super.remove(element)
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
    override fun addAll(elements: Collection<Result>): Boolean {
        val result: Boolean = super.addAll(elements)
        confidence = calculateConfidence()

        return result
    }

    /**
     * Inserts all of the elements of the specified collection [elements] into this list at the specified [index].
     *
     * @return `true` if the list was changed as the result of the operation.
     */
    override fun addAll(index: Int, elements: Collection<Result>): Boolean {
        val result: Boolean = super.addAll(index, elements)
        confidence = calculateConfidence()

        return result
    }

    override fun removeAll(elements: Collection<Result>): Boolean {
        val result: Boolean = super.removeAll(elements)
        confidence = calculateConfidence()

        return result
    }
    override fun retainAll(elements: Collection<Result>): Boolean {
        val result: Boolean = super.retainAll(elements)
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
    override operator fun set(index: Int, element: Result): Result {
        val result: Result = super.set(index, element)
        confidence = calculateConfidence()

        return result
    }

    /**
     * Inserts an element into the list at the specified [index].
     */
    override fun add(index: Int, element: Result): Unit {
        super.add(element)
        confidence = calculateConfidence()
    }

    /**
     * Removes an element at the specified [index] from the list.
     *
     * @return the element that has been removed.
     */
    override fun removeAt(index: Int): Result {
        val result: Result = super.removeAt(index)
        confidence = calculateConfidence()

        return result
    }

    // View
    override fun subList(fromIndex: Int, toIndex: Int): MutableList<Result> {
        val result: MutableList<Result> = super.subList(fromIndex, toIndex)
        confidence = calculateConfidence()

        return result
    }
}

//abstract class MachineLearningArray<Result> : Array<IMachineLearningResult<Result>> {
//
//}1