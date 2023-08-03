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

interface WithConfAndClas<S> : WithConfidence, WithClassification<S>

// with supergroup
interface WithSupergroup<S> {
    val supergroup: S
}

interface WithConfAndGroup<S> : WithConfidence, WithSupergroup<S>


interface IClassificationFinalResult<S> : IMachineLearningFinalResult, WithConfAndGroup<S>


data class ClassificationFinalResult<S> (
    override val confidence: Float,
    override val supergroup: S
) : IClassificationFinalResult<S>


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
interface IClassificationResult<D, R : WithConfAndClas<S>, S> : IMachineLearningResult<D, R> {
    val groups: Set<S>
}

interface IClassificationOutput<D, R : WithConfAndClas<S>, S> : IMachineLearningOutput<D, R>, WithConfAndGroup<S>

data class ClassificationResult<D, R : WithConfAndClas<S>, S>(
    override val groups: Set<S>,
    override val data: D,
    override val listItems: MachineLearningResultList<R>
) : IClassificationResult<D, R, S> {
    override val confidence: Float = listItems.confidence
}

data class ClassificationOutput<D, R : WithConfAndClas<S>, S> (
    override val listPartialResults: MachineLearningResultList<IMachineLearningResult<D, R>>,
    override val supergroup: S,
    override val confidence: Float
) : IClassificationOutput<D, R, S>


// ---------------------------------- TYPE ALIASES ----------------------------------

typealias ClassificationRepository<D, R, S> = MachineLearningRepository<IMachineLearningInput<D>, IClassificationResult<D, R, S>>