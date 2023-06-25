package com.example.driverchecker.machinelearning.data

import kotlinx.serialization.Serializable
// ---------------------------------- CLASSES ----------------------------------

typealias IStringClassification = IClassification<String>

@Serializable
data class StringClassification (
    override val name: String,
    override val index: Int,
    override val superclass: String
) : IStringClassification


// ---------------------------------- TYPE ALIAS ----------------------------------

typealias StringClassificationSuperclassMap = Map<String, Set<IStringClassification>>
typealias StringClassificationSuperclassList = List<Set<IStringClassification>>

typealias StringClassificationSet = Set<IStringClassification>
typealias StringClassificationList = List<IStringClassification>

// ---------------------------------- SERIALIZABLE ----------------------------------
@Serializable
data class StringImportClassifier (val value: Map<String, Set<String>>)