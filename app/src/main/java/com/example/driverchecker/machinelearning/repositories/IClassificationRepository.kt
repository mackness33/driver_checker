package com.example.driverchecker.machinelearning.repositories

import com.example.driverchecker.machinelearning.data.WithConfAndClass
import com.example.driverchecker.machinelearning.data.WithConfAndGroups
import com.example.driverchecker.machinelearning.data.WithConfAndSuper

interface IClassificationRepository<in I, out O : WithConfAndGroups<S>, FR : WithConfAndSuper<S>, S> :
    IMachineLearningRepository<I, O, FR> {
    fun loadClassifications(json: String?) : Boolean
}