package com.example.driverchecker.machinelearning.models

import com.example.driverchecker.machinelearning.data.IClassification
import com.example.driverchecker.machinelearning.helpers.classifiers.IClassifier

interface IClassificationModel <Data, Result, Superclass> : IMachineLearningModel<Data, Result> {
    val classifier: IClassifier<Superclass>
    fun <ModelInit : Map<Superclass, Set<IClassification<Superclass>>>> loadClassifications (init: ModelInit?) : Boolean
    fun loadClassifications(json: String?) : Boolean
}