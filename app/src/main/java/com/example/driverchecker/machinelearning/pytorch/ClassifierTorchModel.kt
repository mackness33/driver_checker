package com.example.driverchecker.machinelearning.pytorch

import com.example.driverchecker.machinelearning.classification.IClassificationModel
import com.example.driverchecker.machinelearning.classification.IClassifier
import com.example.driverchecker.machinelearning.classification.MutableClassifier
import com.example.driverchecker.machinelearning.data.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

typealias ImageDetectionTorchModel<Superclass> = ClassifierTorchModel<IImageDetectionData, ImageDetectionArrayListOutput<Superclass>, Superclass>

abstract class ClassifierTorchModel<Data, Result, Superclass : Comparable<Superclass>> :
    LitePyTorchModel<Data, Result>,
    IClassificationModel<Data, Result, Superclass>
{
    constructor() : super()

    constructor(modelPath: String? = null, classificationsJson: String? = null) : super(modelPath) {
        if (classificationsJson != null) initClassifier(classificationsJson)
    }

    constructor(modelPath: String? = null, newClassifications: ClassificationSuperclassMap<Superclass>? = null) : super(modelPath) {
        if (newClassifications != null) initClassifier(newClassifications)
    }

    protected val _classifier = MutableClassifier<Superclass>(null)
    val classifier: IClassifier<Superclass>
        get() = _classifier

    private fun initClassifier(json: String?) : Boolean = loadClassifications(json)
    private fun initClassifier(newClassifications: ClassificationSuperclassMap<Superclass>?) : Boolean = loadClassifications(newClassifications)

    override fun <ModelInit : ClassificationSuperclassMap<Superclass>> loadClassifications(init: ModelInit?): Boolean {
        return _classifier.load(init)
    }

    override fun loadClassifications(json: String?): Boolean {
        if (json.isNullOrBlank())
            return false

        val importedJson = Json.decodeFromString<ImportClassifier<Superclass>>(json)

        return _classifier.load(importedJson)
    }
}