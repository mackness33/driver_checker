package com.example.driverchecker.machinelearning.manipulators

import androidx.lifecycle.LiveData
import com.example.driverchecker.machinelearning.data.*

interface IClassificationClient<I, O : WithConfAndGroups<S>, FR : WithConfAndSuper<S>, S> : IMachineLearningClient<I, O, FR> {
    // LIVE DATA
    val passengerInfo: LiveData<Pair<Int, Int>>

    val driverInfo: LiveData<Pair<Int, Int>>

    val simpleListClassesPredictions: List<Pair<Int, List<Int>>>

    override val output: LiveData<FR?>
}
