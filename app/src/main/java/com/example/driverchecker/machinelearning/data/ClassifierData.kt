package com.example.driverchecker.machinelearning.data

import kotlinx.serialization.Serializable


// ---------------------------------- SERIALIZABLE ----------------------------------
@Serializable
data class ImportClassifier<Superclass> (val value: Map<Superclass, Set<String>>)

// ---------------------------------- CLASSIFICATION ----------------------------------

interface IClassificationWithMetrics<S> : IClassification<S> {
    val objectsFound: Int
}

interface IMutableClassificationWithMetrics<S> : IClassificationWithMetrics<S> {
    fun inc()
    fun dec()
}

interface IClassification<S> {
    val name: String
    val externalIndex: Int
    val internalIndex: Int
    val supergroup: S
}


@Serializable
data class Classification<S> (
    override val name: String,
    override val externalIndex: Int,
    override val internalIndex: Int,
    override val supergroup: S,
) : IClassification<S>

data class ClassificationWithMetrics<S> (
    override val name: String,
    override val externalIndex: Int,
    override val internalIndex: Int,
    override val supergroup: S,
    override val objectsFound: Int,
) : IClassificationWithMetrics<S>

class MutableClassificationWithMetrics<S> : IMutableClassificationWithMetrics<S> {
    override val name: String
    override val externalIndex: Int
    override val supergroup: S
    override val internalIndex: Int
    override var objectsFound: Int

    constructor (
        name: String,
        index: Int,
        internalIndex: Int = 0,
        supergroup: S,
        objectsFound: Int = 1,
    ) {
        this.name = name
        this.externalIndex = index
        this.supergroup = supergroup
        this.internalIndex = internalIndex
        this.objectsFound = objectsFound
    }

    constructor (classification: IClassification<S>, objFound: Int = 1) : this(
        classification.name,
        classification.externalIndex,
        classification.internalIndex,
        classification.supergroup,
        objFound
    )

    override fun inc() {
        objectsFound++
    }

    override fun dec() {
        objectsFound--
    }

    fun toReadOnly() : ClassificationWithMetrics<S> = ClassificationWithMetrics(
        this.name,
        this.externalIndex,
        this.internalIndex,
        this.supergroup,
        this.objectsFound
    )
}
