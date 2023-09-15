package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.machinelearning.helpers.classifiers.IClassifier
import com.example.driverchecker.machinelearning.helpers.classifiers.MutableClassifier
import com.example.driverchecker.utils.ISettings
import kotlinx.serialization.Serializable
import kotlin.time.Duration


// ---------------------------------- CLASSES ----------------------------------

// with classification
interface WithClassification<S> {
    val classification: IClassification<S>
}

// with supergroup
interface WithSupergroup<S> {
    val supergroup: S
}

// with supergroup
interface WithGroups<S> {
    val groups: Map<S, Set<IClassificationWithMetrics<S>>>
}


interface IClassificationMetrics<S> : IMetrics {
    val groupMetrics: Map<S, Triple<Int, Int, Int>>?
}

data class ClassificationMetrics<S> (
    override val totalTime: Duration,
    override val totalWindows: Int,
    override val groupMetrics: Map<S, Triple<Int, Int, Int>>? = null,
) : IClassificationMetrics<S> {
    constructor (main: IMetrics, groupMetrics: Map<S, Triple<Int, Int, Int>>? = null) : this (
        main.totalTime,
        main.totalWindows,
        groupMetrics
    )
}


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


// ---------------------------------- TYPE ALIAS ----------------------------------

typealias ClassificationSupergroupMap<Superclass> = Map<Superclass, ClassificationSet<Superclass>>
typealias ClassificationSuperclassList<Superclass> = List<ClassificationSet<Superclass>>

typealias ClassificationSet<Superclass> = Set<IClassification<Superclass>>
typealias ClassificationList<Superclass> = List<IClassification<Superclass>>

// ---------------------------------- SERIALIZABLE ----------------------------------
@Serializable
data class ImportClassifier<Superclass> (val value: Map<Superclass, Set<String>>)

typealias StringMutableClassifier = MutableClassifier<String>
typealias StringClassifier = IClassifier<String>


// ---------------------------------- BASIC OUTPUT ----------------------------------
// with classification
interface IClassificationItem<S> : IMachineLearningItem, WithClassification<S> {
    override val classification: IClassification<S>
}

interface IClassificationOutputStats<S> : IMachineLearningOutputStats, WithGroups<S>

interface IClassificationOutput<E : IClassificationItem<S>, S> : IMachineLearningOutput<E>, IClassificationOutputStats<S> {
    override val listItems: ClassificationItemList<E, S>
}

interface IClassificationFinalResultStats<S> : IMachineLearningFinalResultStats, WithSupergroup<S>

interface IClassificationFinalResult<S> : IMachineLearningFinalResult, IClassificationFinalResultStats<S> {
    override val metrics: IClassificationMetrics<S>?
}


data class ClassificationItem<S> (
    override val confidence: Float,
    override val classification: IClassification<S>,
) : IClassificationFullItem<S> {
    constructor(baseResult: IClassificationItem<S>) : this(baseResult.confidence, baseResult.classification)
}

data class ClassificationOutputStats<S> (
    override val confidence: Float,
    override val groups: Map<S, Set<IClassificationWithMetrics<S>>>
) : IClassificationOutputStats<S>

data class ClassificationOutput<E : IClassificationItem<S>, S> (
    override val listItems: ClassificationItemList<E, S>
) : IClassificationOutput<E, S> {
    override val confidence: Float = listItems.confidence
    override val groups: Map<S, Set<IClassificationWithMetrics<S>>> = listItems.groups
}

data class ClassificationFinalResult<S> (
    override val confidence: Float,
    override val supergroup: S,
    override val settings: ISettings? = null,
    override val metrics: IClassificationMetrics<S>? = null,
) : IClassificationFinalResult<S> {
    constructor(baseResult: IClassificationFinalResult<S>) : this(
        baseResult.confidence, baseResult.supergroup, baseResult.settings, baseResult.metrics
    )

    constructor(main: IClassificationFinalResultStats<S>, settings: ISettings?, metrics: IClassificationMetrics<S>?) : this (
        main.confidence, main.supergroup, settings, metrics
    )
}

// ---------------------------------- FULL OUTPUT ----------------------------------

interface IClassificationFullItem<S> : IMachineLearningFullItem, IClassificationItem<S>

interface IClassificationFullOutput<I, E : IClassificationFullItem<S>, S> : IMachineLearningFullOutput<I, E>, IClassificationOutput<E, S>

interface IClassificationFullFinalResult<S> : IMachineLearningFullFinalResult, IClassificationFinalResult<S>


data class ClassificationFullItem<S> (
    override val confidence: Float,
    override val classification: IClassification<S>,
) : IClassificationFullItem<S> {
    constructor(baseResult: IClassificationItem<S>) : this(baseResult.confidence, baseResult.classification)
}

data class ClassificationFullOutput<I, E : IClassificationFullItem<S>, S> (
    override val input: I,
    override val listItems: ClassificationItemList<E, S>
) : IClassificationFullOutput<I, E, S> {
    override val confidence: Float = listItems.confidence
    override val groups: Map<S, Set<IClassificationWithMetrics<S>>> = listItems.groups
}

data class ClassificationFullFinalResult<S> (
    override val confidence: Float,
    override val supergroup: S,
    override val settings: ISettings? = null,
    override val metrics: IClassificationMetrics<S>? = null,
) : IClassificationFullFinalResult<S> {
    constructor(baseResult: IClassificationFinalResult<S>) : this(
        baseResult.confidence, baseResult.supergroup, baseResult.settings, baseResult.metrics
    )
}


// ---------------------------------- TYPE ALIASES ----------------------------------

//typealias ClassificationRepository<D, R, S> = MachineLearningRepository<IMachineLearningInput<D>, IClassificationResult<D, R, S>>


sealed interface LiveClassificationStateInterface : LiveEvaluationStateInterface

// Represents different states for the LatestNews screen
sealed class LiveClassificationState : LiveEvaluationState(), LiveClassificationStateInterface {
    data class Start<S>(val maxClassesPerGroup: Int, val classifier: IClassifier<S>) : LiveClassificationStateInterface
    data class Loading<S>(val index: Int, val partialResult: IClassificationOutputStats<S>?) : LiveClassificationStateInterface
    data class End<S>(val exception: Throwable?, val finalResult: IClassificationFinalResult<S>?) : LiveClassificationStateInterface
}