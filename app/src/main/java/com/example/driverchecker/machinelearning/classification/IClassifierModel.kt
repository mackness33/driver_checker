package com.example.driverchecker.machinelearning.classification

import com.example.driverchecker.machinelearning.data.ClassificationSuperclassMap
import com.example.driverchecker.machinelearning.general.IMachineLearningModel

interface IClassifierModel<Data, Result, Superclass> : IMachineLearningModel<Data, Result> {
    fun loadClassifications(newClassifications: ClassificationSuperclassMap<Superclass>?) : Boolean
    fun loadClassifications(json: String?) : Boolean
}