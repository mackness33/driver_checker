package com.example.driverchecker.machinelearning.classification

import com.example.driverchecker.machinelearning.general.IMachineLearningModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface IClassificationModel <Data, Result> : IMachineLearningModel<Data, Result> {
    fun <ModelInit> loadClassifications (init: ModelInit)
    fun loadClassifications(json: String?) : Boolean
}