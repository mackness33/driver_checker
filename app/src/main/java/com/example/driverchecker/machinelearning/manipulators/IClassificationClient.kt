package com.example.driverchecker.machinelearning.manipulators

import androidx.lifecycle.LiveData
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.classifiers.IClassifier

interface IClassificationClient<I, O : WithConfAndGroups<S>, FR : WithConfAndSuper<S>, S> : IMachineLearningClient<I, O, FR> {
    // LIVE DATA
    override val output: LiveData<FR?>

    val metricsPerGroup: Map<S, LiveData<Pair<Int, Int>>>
    val classifier: IClassifier<S>?
    val groups: LiveData<Set<S>>
}
