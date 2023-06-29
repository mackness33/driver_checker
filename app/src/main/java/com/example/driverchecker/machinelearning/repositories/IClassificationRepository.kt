package com.example.driverchecker.machinelearning.repositories

interface IClassificationRepository<in Data, out Result> :
    IMachineLearningRepository<Data, Result> {
    fun loadClassifications(json: String?) : Boolean
}