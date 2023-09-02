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
    val groups: Map<S, Int>
}

interface IClassification<S> {
    val name: String
    val index: Int
    val supergroup: S
}

@Serializable
data class Classification<S> (
    override val name: String,
    override val index: Int,
    override val supergroup: S
) : IClassification<S>


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

interface IClassificationOutput<I, E : IClassificationItem<S>, S> : IMachineLearningOutput<I, E>, WithConfAndGroups<S>
//interface IClassificationOutput<I, E : IClassificationItem<S>, S> : IMachineLearningOutput<I, E> {
//    val groups: Set<S>
//}

interface IClassificationFinalResult<S> : IMachineLearningFinalResult, WithConfAndSuper<S>
//interface IClassificationFinalResult<S> : IMachineLearningFinalResult {
//    val supergroup: S
//}


data class ClassificationItem<S> (
    override val confidence: Float,
    override val classification: IClassification<S>,
) : IClassificationItem<S>

data class ClassificationOutput<I, E : IClassificationItem<S>, S> (
    override val groups: Map<S, Int>,
    override val input: I,
    override val listItems: MachineLearningItemList<E>
) : IClassificationOutput<I, E, S> {
    override val confidence: Float = listItems.confidence
}

data class ClassificationFinalResult<S> (
    override val confidence: Float,
    override val supergroup: S
) : IClassificationFinalResult<S>


// ---------------------------------- TYPE ALIASES ----------------------------------

//typealias ClassificationRepository<D, R, S> = MachineLearningRepository<IMachineLearningInput<D>, IClassificationResult<D, R, S>>


sealed interface LiveClassificationStateInterface : LiveEvaluationStateInterface

// Represents different states for the LatestNews screen
sealed class LiveClassificationState : LiveEvaluationState(), LiveClassificationStateInterface {
    data class Start<S>(val maxClassesPerGroup: Int, val supergroups: List<S>) : LiveClassificationStateInterface
    data class Loading<S>(val index: Int, val partialResult: WithConfAndGroups<S>?) : LiveClassificationStateInterface
    data class End<S>(val exception: Throwable?, val finalResult: WithConfAndSuper<S>?) : LiveClassificationStateInterface
}