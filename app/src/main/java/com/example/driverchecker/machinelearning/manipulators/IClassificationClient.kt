package com.example.driverchecker.machinelearning.manipulators

import androidx.lifecycle.LiveData
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.classifiers.IClassifier
import com.example.driverchecker.utils.StateLiveData

interface IClassificationClient<I, O : WithConfAndGroups<S>, FR : WithConfAndSuper<S>, S> : IMachineLearningClient<I, O, FR> {
    // LIVE DATA
    override val finalResult: StateLiveData<FR?>

    val metricsPerGroup: ClientMetricsMap<S>
    val classifier: IClassifier<S>?
    val groups: StateLiveData<Set<S>>
    val areMetricsObservable: LiveData<Boolean>
}
