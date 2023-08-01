package com.example.driverchecker.machinelearning.manipulators

import androidx.lifecycle.LiveData
import com.example.driverchecker.machinelearning.data.PartialEvaluationStateInterface
import com.example.driverchecker.machinelearning.data.WithConfidence

interface IClassificationClient<D, R : WithConfidence> : IMachineLearningClient<D, R> {
    // LIVE DATA
    val passengerInfo: LiveData<Pair<Int, Int>>

    val driverInfo: LiveData<Pair<Int, Int>>

    val simpleListClassesPredictions: List<Pair<Int, List<Int>>>
}
