package com.example.driverchecker.machinelearning.models.pytorch

import android.util.Log
import com.example.driverchecker.machinelearning.models.IClassificationModel
import com.example.driverchecker.machinelearning.helpers.classifiers.IClassifier
import com.example.driverchecker.machinelearning.helpers.classifiers.MutableClassifier
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.producers.AProducer
import com.example.driverchecker.machinelearning.helpers.producers.IClassificationProducer
import com.example.driverchecker.machinelearning.helpers.producers.IModelStateProducer
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json

abstract class ClassifierTorchModel<I, O, S : Comparable<S>> :
    LitePyTorchModel<I, O>,
    IClassificationModel<I, O, S>
{
    constructor(scope: CoroutineScope) : super(scope)
    constructor(modelPath: String? = null, classificationsJson: String? = null, scope: CoroutineScope) : super(modelPath, scope) {
//        if (classificationsJson != null) initClassifier(classificationsJson)
    }
    constructor(modelPath: String? = null, newClassifications: Map<S, Set<IClassification<S>>>? = null, scope: CoroutineScope) : super(modelPath, scope) {
//        if (newClassifications != null) initClassifier(newClassifications)
    }


    override val modelStateProducer: IClassificationProducer<Boolean> = ClassificationStateProducer()
    protected val mClassifier = MutableClassifier<S>(null)
    override val classifier: IClassifier<S>
        get() = mClassifier

    protected fun initClassifier(json: String?) : Boolean {
        return loadClassifications(json)
    }
    protected fun initClassifier(newClassifications: Map<S, Set<IClassification<S>>>?) : Boolean
        = runBlocking {
            loadClassifications(newClassifications)
        }

    override suspend fun <ModelInit : Map<S, Set<IClassification<S>>>> loadClassifications(init: ModelInit?): Boolean {
        val result = mClassifier.load(init)
        modelStateProducer.classificationReady(result)

        return result
    }

    override fun loadClassifications(json: String?): Boolean {
        if (json.isNullOrBlank()) {
            modelStateProducer.classificationReady(false)
            return false
        }

        try {
            val importedJson = Json.decodeFromString<ImportClassifier<S>>(json)
            val result = mClassifier.load(importedJson)
            modelStateProducer.classificationReady(result)
        } catch (e : Throwable) {
            Log.e("ClassifierTorchModel", e.message.toString(), e)
            modelStateProducer.classificationReady(false)
        }

        return true
    }

    protected open inner class ClassificationStateProducer :
        ModelStateProducer(),
        IClassificationProducer<Boolean>
    {
        override fun classificationReady(isReady: Boolean) = runBlocking {
            readyMap["classification"] = isReady
            updateState()
        }

        override fun initialize () {
            readyMap["classification"] = false
            super.initialize()
        }
    }
}