package com.example.driverchecker.machinelearning.general

import com.example.driverchecker.machinelearning.data.ClassificationSuperclassMap

interface IClassifierModel<Data, Result, Superclass> : MLModelInterface<Data, Result> {
    fun loadClassifications(newClassifications: ClassificationSuperclassMap<Superclass>?) : Boolean
}