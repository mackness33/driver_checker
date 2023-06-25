package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.machinelearning.classification.IClassifier
import com.example.driverchecker.machinelearning.classification.MutableClassifier
import com.example.driverchecker.machinelearning_old.data.IMachineLearningResultOld
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

interface IClassificationResult<Result, Superclass> : IMachineLearningResult<Result> {
    val group: IClassification<Superclass>
}

interface IClassificationResultWithInput<Data, Result, Superclass> : IClassificationResult<Result, Superclass>, IMachineLearningData<Data>

data class ClassificationBaseOutput<Result, Superclass>(
    override val result: Result,
    override val confidence: Float,
    override val group: IClassification<Superclass>
) : IClassificationResult<Result, Superclass>

data class ClassificationOutput<Data, Result, Superclass>(
    override val result: Result,
    override val confidence: Float,
    override val data: Data,
    override val group: IClassification<Superclass>
) : IClassificationResultWithInput<Data, Result, Superclass>


// ---------------------------------- TYPE ALIASES ----------------------------------

typealias ClassificationArrayOutput<Data, Result, Superclass> = Array<IClassificationResultWithInput<Data, Result, Superclass>>
typealias ClassificationArrayBaseOutput<Result, Superclass> = Array<IClassificationResult<Result, Superclass>>

typealias ClassificationListOutput<Data, Result, Superclass> = List<IClassificationResultWithInput<Data, Result, Superclass>>
typealias ClassificationListBaseOutput<Result, Superclass> = List<IClassificationResult<Result, Superclass>>

typealias ClassificationArrayListOutput<Data, Result, Superclass> = ArrayList<IClassificationResultWithInput<Data, Result, Superclass>>
typealias ClassificationArrayListBaseOutput<Result, Superclass> = ArrayList<IClassificationResult<Result, Superclass>>