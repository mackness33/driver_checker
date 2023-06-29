package com.example.driverchecker.machinelearning.models

import com.example.driverchecker.machinelearning.data.IClassification

interface IClassificationModel <Data, Result, Superclass> : IMachineLearningModel<Data, Result> {
    fun <ModelInit : Map<Superclass, Set<IClassification<Superclass>>>> loadClassifications (init: ModelInit?) : Boolean
    fun loadClassifications(json: String?) : Boolean
}