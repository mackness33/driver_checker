package com.example.driverchecker.machinelearning.models.pytorch

import com.example.driverchecker.machinelearning.models.IClassificationModel
import com.example.driverchecker.machinelearning.helpers.classifiers.IClassifier
import com.example.driverchecker.machinelearning.helpers.classifiers.MutableClassifier
import com.example.driverchecker.machinelearning.data.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

abstract class ClassifierTorchModel<I, O, S : Comparable<S>> :
    LitePyTorchModel<I, O>,
    IClassificationModel<I, O, S>
{
    constructor() : super()

    constructor(modelPath: String? = null, classificationsJson: String? = null) : super(modelPath) {
        if (classificationsJson != null) initClassifier(classificationsJson)
    }

    constructor(modelPath: String? = null, newClassifications: Map<S, Set<IClassification<S>>>? = null) : super(modelPath) {
        if (newClassifications != null) initClassifier(newClassifications)
    }

    protected val mClassifier = MutableClassifier<S>(null)
    override val classifier: IClassifier<S>
        get() = mClassifier

    private fun initClassifier(json: String?) : Boolean = loadClassifications(json)
    private fun initClassifier(newClassifications: Map<S, Set<IClassification<S>>>?) : Boolean = loadClassifications(newClassifications)

    override fun <ModelInit : Map<S, Set<IClassification<S>>>> loadClassifications(init: ModelInit?): Boolean {
        return mClassifier.load(init)
    }

    override fun loadClassifications(json: String?): Boolean {
        if (json.isNullOrBlank())
            return false

        // TODO: For now ImportClassifier can "understand" only String for simplicity
        val importedJson = Json.decodeFromString<ImportClassifier<S>>(json)

        return mClassifier.load(importedJson)
    }
}