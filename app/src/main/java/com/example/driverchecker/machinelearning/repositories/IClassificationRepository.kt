package com.example.driverchecker.machinelearning.repositories

import com.example.driverchecker.machinelearning.data.WithConfAndSupergroup

interface IClassificationRepository<in Data, out Result : WithConfAndSupergroup<S>, S> :
    IMachineLearningRepository<Data, Result> {
    fun loadClassifications(json: String?) : Boolean
}