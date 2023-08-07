package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.machinelearning.helpers.classifiers.IClassifier
import com.example.driverchecker.machinelearning.helpers.classifiers.MutableClassifier
import com.example.driverchecker.machinelearning.repositories.general.MachineLearningRepository
import kotlinx.serialization.Serializable



// ---------------------------------- CLASSES ----------------------------------

// with classification
interface WithClassification<S> {
    val classification: IClassification<S>
}

interface WithConfAndClass<S> : WithConfidence, WithClassification<S>

// with supergroup
interface WithSupergroup<S> {
    val supergroup: S
}

interface WithConfAndSuper<S> : WithConfidence, WithSupergroup<S>


// with supergroup
interface WithGroups<S> {
    val groups: Set<S>
}

interface WithConfAndGroups<S> : WithConfidence, WithGroups<S>


data class ClassificationFinalResult<S> (
    override val confidence: Float,
    override val supergroup: S
) : WithConfAndSuper<S>


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

typealias ClassificationSuperclassMap<Superclass> = Map<Superclass, Set<IClassification<Superclass>>>
typealias ClassificationSuperclassList<Superclass> = List<Set<IClassification<Superclass>>>

typealias ClassificationSet<Superclass> = Set<IClassification<Superclass>>
typealias ClassificationList<Superclass> = List<IClassification<Superclass>>

// ---------------------------------- SERIALIZABLE ----------------------------------
@Serializable
data class ImportClassifier<Superclass> (val value: Map<Superclass, Set<String>>)

typealias StringMutableClassifier = MutableClassifier<String>
typealias StringClassifier = IClassifier<String>


// ---------------------------------- OUTPUT ----------------------------------
interface IClassificationResult<D, R : WithConfAndClass<S>, S> : IMachineLearningResult<D, R>, WithConfAndGroups<S>

interface IClassificationOutput<D, R : WithConfAndGroups<S>, S> : IMachineLearningOutput<D, R>, WithConfAndSuper<S>

data class ClassificationResult<D, R : WithConfAndClass<S>, S> (
    override val groups: Set<S>,
    override val data: D,
    override val listItems: MachineLearningResultList<R>
) : IClassificationResult<D, R, S> {
    override val confidence: Float = listItems.confidence
}

data class ClassificationOutput<D, R : WithConfAndGroups<S>, S> (
    override val listPartialResults: MachineLearningResultList<R>,
    override val supergroup: S,
    override val confidence: Float
) : IClassificationOutput<D, R, S>


// ---------------------------------- TYPE ALIASES ----------------------------------

typealias ClassificationRepository<D, R, S> = MachineLearningRepository<IMachineLearningInput<D>, IClassificationResult<D, R, S>>


sealed interface LiveClassificationStateInterface : LiveEvaluationStateInterface

// Represents different states for the LatestNews screen
//sealed class LiveClassificationState<R : WithConfidence, S> : LiveEvaluationState<R>(), LiveClassificationStateInterface<R, S> {
//    data class Start(val maxClassesPerGroup: Int) : LiveClassificationState<Nothing, Nothing>()
//    data class End<S>(val exception: Throwable?, val finalResult: WithConfAndSuper<S>?) : LiveClassificationState<Nothing, S>()
//}

// Represents different states for the LatestNews screen
sealed class LiveClassificationState : LiveEvaluationState(), LiveClassificationStateInterface {
    data class Start(val maxClassesPerGroup: Int) : LiveClassificationStateInterface
    data class End<S>(val exception: Throwable?, val finalResult: WithConfAndSuper<S>?) : LiveClassificationStateInterface
}