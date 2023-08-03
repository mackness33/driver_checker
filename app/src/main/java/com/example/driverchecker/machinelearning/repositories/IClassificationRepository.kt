package com.example.driverchecker.machinelearning.repositories

import com.example.driverchecker.machinelearning.data.WithConfAndClas

interface IClassificationRepository<in Data, out Result : WithConfAndClas<S>, S> :
    IMachineLearningRepository<Data, Result> {
    fun loadClassifications(json: String?) : Boolean
}