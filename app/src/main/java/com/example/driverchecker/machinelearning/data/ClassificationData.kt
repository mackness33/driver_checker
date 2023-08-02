package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.machinelearning.helpers.classification.IClassifier
import com.example.driverchecker.machinelearning.helpers.classification.MutableClassifier
import com.example.driverchecker.machinelearning.repositories.general.MachineLearningRepository
import kotlinx.serialization.Serializable
// ---------------------------------- CLASSES ----------------------------------

interface IClassification<Superclass> {
    val name: String
    val index: Int
    val superclass: Superclass
}

@Serializable
data class Classification<Superclass> (
    override val name: String,
    override val index: Int,
    override val superclass: Superclass
) : IClassification<Superclass>


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

interface IClassificationBasicItem<Result, Superclass> : IMachineLearningBasicItem<Result> {
    val group: IClassification<Superclass>
}

interface IClassificationBasicItemWithInput<Data, Result, Superclass> : IClassificationBasicItem<Result, Superclass>, IMachineLearningInput<Data>


interface IClassificationResult<D, R : WithConfidence, S> : IMachineLearningResult<D, R> {
    val group: IClassification<S>
}

interface IClassificationOutput<D, R : WithConfidence, S> : IMachineLearningOutput<D, R>

data class ClassificationResult<D, R : WithConfidence, S>(
    override val group: IClassification<S>,
    override val data: D,
    override val listItems: MachineLearningResultList<R>
) : IClassificationResult<D, R, S>

data class ClassificationOutput<D, R : WithConfidence, S>(
    override val listPartialResults: MachineLearningResultList<IMachineLearningResult<D, R>>
) : IClassificationOutput<D, R, S>


// ---------------------------------- TYPE ALIASES ----------------------------------

typealias ClassificationListOutput<Data, Result, Superclass> = MachineLearningResultList<IClassificationBasicItemWithInput<Data, Result, Superclass>>
typealias ClassificationListBaseOutput<Result, Superclass> = MachineLearningResultList<IClassificationBasicItem<Result, Superclass>>

typealias ClassificationArrayListOutput<Data, Result, Superclass> = MachineLearningResultArrayList<IClassificationBasicItemWithInput<Data, Result, Superclass>>
typealias ClassificationArrayListBaseOutput<Result, Superclass> = MachineLearningResultArrayList<IClassificationBasicItem<Result, Superclass>>

typealias ClassificationRepository<Data, Result, Superclass> = MachineLearningRepository<IMachineLearningInput<Data>, ClassificationArrayListOutput<Data, Result, Superclass>>