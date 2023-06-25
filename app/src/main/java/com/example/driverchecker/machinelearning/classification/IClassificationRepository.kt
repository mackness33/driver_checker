package com.example.driverchecker.machinelearning.classification

import com.example.driverchecker.machinelearning.general.IMachineLearningRepository

interface IClassificationRepository<in Data, out Result> :
    IMachineLearningRepository<Data, Result> {
    fun loadClassifications(json: String?) : Boolean
}