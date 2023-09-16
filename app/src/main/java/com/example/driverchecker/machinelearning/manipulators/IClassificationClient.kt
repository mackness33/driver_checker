package com.example.driverchecker.machinelearning.manipulators

import androidx.lifecycle.LiveData
import com.example.driverchecker.machinelearning.collections.ClassificationMetricsMap
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.classifiers.IClassifier
import com.example.driverchecker.utils.StateLiveData

interface IClassificationClient<I, O : IClassificationOutputStats<S>, FR : IClassificationFinalResult<S>, S> : IMachineLearningClient<I, O, FR> {
    // LIVE DATA
    override val finalResult: StateLiveData<FR?>

    val metricsPerGroup: ClassificationMetricsMap<S>
    val classifier: IClassifier<S>?
    val groups: StateLiveData<Set<S>>
    val areMetricsObservable: LiveData<Boolean>
    val lastMetricsPerGroup: Map<S, Triple<Int, Int, Int>?>
}
