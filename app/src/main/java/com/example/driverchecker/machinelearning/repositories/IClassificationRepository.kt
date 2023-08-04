package com.example.driverchecker.machinelearning.repositories

import com.example.driverchecker.machinelearning.data.WithConfAndClass
import com.example.driverchecker.machinelearning.data.WithConfAndGroups

interface IClassificationRepository<in Data, out Result : WithConfAndGroups<S>, S> :
    IMachineLearningRepository<Data, Result> {
    fun loadClassifications(json: String?) : Boolean
}