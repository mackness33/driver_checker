package com.example.driverchecker.machinelearning.models

import com.example.driverchecker.machinelearning.data.IClassification
import com.example.driverchecker.machinelearning.helpers.classifiers.IClassifier

interface IClassificationModel <I, O, S> : IMachineLearningModel<I, O> {
    val classifier: IClassifier<S>
    fun <ModelInit : Map<S, Set<IClassification<S>>>> loadClassifications (init: ModelInit?) : Boolean
    fun loadClassifications(json: String?) : Boolean
}