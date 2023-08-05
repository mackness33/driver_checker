package com.example.driverchecker.machinelearning.manipulators

import androidx.lifecycle.LiveData
import com.example.driverchecker.machinelearning.data.*

interface IClassificationClient<D, R : WithConfAndGroups<S>, S> : IMachineLearningClient<D, R> {
    // LIVE DATA
    val passengerInfo: LiveData<Pair<Int, Int>>

    val driverInfo: LiveData<Pair<Int, Int>>

    val simpleListClassesPredictions: List<Pair<Int, List<Int>>>

    override fun getOutput () : IClassificationOutput<D, R, S>?
}
