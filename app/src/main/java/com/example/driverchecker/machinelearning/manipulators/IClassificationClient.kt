package com.example.driverchecker.machinelearning.manipulators

import androidx.lifecycle.LiveData
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.classifiers.IClassifier
import com.example.driverchecker.utils.ObservableData

interface IClassificationClient<I, O : IClassificationOutput<S>, FR : IClassificationFinalResult<S>, S> : IMachineLearningClient<I, O, FR> {
    // LIVE DATA
    override val finalResult: ObservableData<FR?>

    val metricsPerGroup: IObservableGroupMetrics<S>
    val classifier: IClassifier<S>?
    val groups: ObservableData<Set<S>>
    val areMetricsObservable: LiveData<Boolean>
    val lastMetricsPerGroup: Map<S, Triple<Int, Int, Int>?>

//    override val lastEvaluationData: Map<IWindowBasicData, IGroupMetrics<S>?>
}
