package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.machinelearning.helpers.classifiers.IClassifier
import com.example.driverchecker.machinelearning.helpers.classifiers.MutableClassifier
import kotlinx.serialization.Serializable



// ---------------------------------- CLASSES ----------------------------------

// with classification
interface WithConfAndClass<S> : WithConfidence {
    val classification: IClassification<S>
}

// with supergroup
interface WithConfAndSuper<S> : WithConfidence {
    val supergroup: S
}

// with supergroup
interface WithConfAndGroups<S> : WithConfidence {
    val groups: Map<S, Set<IClassificationMetrics<S>>>
}

interface IClassificationMetrics<S> : IClassification<S> {
    val objectsFound: Int
}

interface IMutableClassificationMetrics<S> : IClassificationMetrics<S> {
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

data class ClassificationMetrics<S> (
    override val name: String,
    override val externalIndex: Int,
    override val internalIndex: Int,
    override val supergroup: S,
    override val objectsFound: Int,
) : IClassificationMetrics<S>

class MutableClassificationMetrics<S> : IMutableClassificationMetrics<S> {
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

    fun toReadOnly() : ClassificationMetrics<S> = ClassificationMetrics(
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


// ---------------------------------- OUTPUT ----------------------------------

interface IClassificationItem<S> : IMachineLearningItem, WithConfAndClass<S>
//interface IClassificationItem<S> : IMachineLearningItem {
//    val classification: IClassification<S>
//}

interface IClassificationOutput<I, E : IClassificationItem<S>, S> : IMachineLearningOutput<I, E>, WithConfAndGroups<S> {
    override val listItems: ClassificationItemList<E, S>
}
//interface IClassificationOutput<I, E : IClassificationItem<S>, S> : IMachineLearningOutput<I, E> {
//    val groups: Set<S>
//}

interface IClassificationFinalResult<S> : IMachineLearningFinalResult, WithConfAndSuper<S> {
    override val listOutputs: List<WithConfAndGroups<S>>
}
//interface IClassificationFinalResult<S> : IMachineLearningFinalResult {
//    val supergroup: S
//}


data class ClassificationItem<S> (
    override val confidence: Float,
    override val classification: IClassification<S>,
) : IClassificationItem<S> {
    constructor(baseResult: WithConfAndClass<S>) : this(baseResult.confidence, baseResult.classification)
}

data class ClassificationOutput<I, E : IClassificationItem<S>, S> (
    override val input: I,
    override val listItems: ClassificationItemList<E, S>
) : IClassificationOutput<I, E, S> {
    override val confidence: Float = listItems.confidence
    override val groups: Map<S, Set<IClassificationMetrics<S>>> = listItems.groups
}

data class ClassificationFinalResult<S> (
    override val confidence: Float,
    override val supergroup: S,
    override val listOutputs: List<WithConfAndGroups<S>>,
) : IClassificationFinalResult<S> {
    constructor(baseResult: WithConfAndSuper<S>, outputs: List<WithConfAndGroups<S>>) : this(baseResult.confidence, baseResult.supergroup, outputs)
}


// ---------------------------------- TYPE ALIASES ----------------------------------

//typealias ClassificationRepository<D, R, S> = MachineLearningRepository<IMachineLearningInput<D>, IClassificationResult<D, R, S>>


sealed interface LiveClassificationStateInterface : LiveEvaluationStateInterface

// Represents different states for the LatestNews screen
sealed class LiveClassificationState : LiveEvaluationState(), LiveClassificationStateInterface {
    data class Start<S>(val maxClassesPerGroup: Int, val classifier: IClassifier<S>) : LiveClassificationStateInterface
    data class Loading<S>(val index: Int, val partialResult: WithConfAndGroups<S>?) : LiveClassificationStateInterface
    data class End<S>(val exception: Throwable?, val finalResult: WithConfAndSuper<S>?) : LiveClassificationStateInterface
}