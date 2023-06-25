package com.example.driverchecker.machinelearning.classification

import com.example.driverchecker.machinelearning.data.ClassificationSuperclassMap
import com.example.driverchecker.machinelearning_old.general.MLModelInterface

interface IClassifierModel<Data, Result, Superclass> : MLModelInterface<Data, Result> {
    fun loadClassifications(newClassifications: ClassificationSuperclassMap<Superclass>?) : Boolean
    fun loadClassifications(json: Superclass?) : Boolean
}