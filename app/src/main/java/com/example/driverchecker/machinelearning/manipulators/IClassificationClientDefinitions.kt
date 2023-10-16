package com.example.driverchecker.machinelearning.manipulators

import androidx.lifecycle.LiveData
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.classifiers.IClassifier
import com.example.driverchecker.utils.ObservableData

interface IClassificationClient<I, O : IClassificationOutput<S>, FR : IClassificationFinalResult<S>, S>
    : IMachineLearningClient<I, O, FR>, IClassificationLiveClient<I, O, S>, IClassificationLastClient<I, O, FR, S> {
    val classifier: IClassifier<S>?
}


interface IClassificationLastClient<I, O, FR, S> : ILastClient<I, O, FR> {
    val lastMetricsPerGroup: Map<S, Triple<Int, Int, Int>?>
}


interface IClassificationLiveClient<I, O, S> : ILiveClient<I, O> {
    val metricsPerGroup: IObservableGroupMetrics<S>
    val groups: ObservableData<Set<S>>
    val areMetricsObservable: LiveData<Boolean>
}
