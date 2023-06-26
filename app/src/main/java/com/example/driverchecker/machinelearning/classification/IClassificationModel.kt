package com.example.driverchecker.machinelearning.classification

import com.example.driverchecker.machinelearning.data.IClassification
import com.example.driverchecker.machinelearning.general.IMachineLearningModel

interface IClassificationModel <Data, Result, Superclass> : IMachineLearningModel<Data, Result> {
    fun <ModelInit : Map<Superclass, Set<IClassification<Superclass>>>> loadClassifications (init: ModelInit?) : Boolean
    fun loadClassifications(json: String?) : Boolean
}