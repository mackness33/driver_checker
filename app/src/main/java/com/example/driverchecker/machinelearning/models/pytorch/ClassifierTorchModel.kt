package com.example.driverchecker.machinelearning.models.pytorch

import com.example.driverchecker.machinelearning.models.IClassificationModel
import com.example.driverchecker.machinelearning.helpers.classifiers.IClassifier
import com.example.driverchecker.machinelearning.helpers.classifiers.MutableClassifier
import com.example.driverchecker.machinelearning.data.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

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

    protected val mClassifier = MutableClassifier<Superclass>(null)
    override val classifier: IClassifier<Superclass>
        get() = mClassifier

    private fun initClassifier(json: String?) : Boolean = loadClassifications(json)
    private fun initClassifier(newClassifications: ClassificationSuperclassMap<Superclass>?) : Boolean = loadClassifications(newClassifications)

    override fun <ModelInit : ClassificationSuperclassMap<Superclass>> loadClassifications(init: ModelInit?): Boolean {
        return mClassifier.load(init)
    }

    override fun loadClassifications(json: String?): Boolean {
        if (json.isNullOrBlank())
            return false

        // TODO: For now ImportClassifier can "understand" only String for simplicity
        val importedJson = Json.decodeFromString<ImportClassifier<Superclass>>(json)

        return mClassifier.load(importedJson)
    }
}