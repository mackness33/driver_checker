package com.example.driverchecker.machinelearning.models

import com.example.driverchecker.machinelearning.data.IClassification
import com.example.driverchecker.machinelearning.helpers.classifiers.IClassifier

interface IClassificationModel <I, O, S> : IMachineLearningModel<I, O> {
    val classifier: IClassifier<S>
    fun loadClassifications(json: String?) : Boolean
}