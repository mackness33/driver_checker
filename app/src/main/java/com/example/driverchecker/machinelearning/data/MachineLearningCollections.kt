package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.utils.MutableStateLiveData
import com.example.driverchecker.utils.StatefulLiveData
import com.example.driverchecker.utils.StateLiveData

interface MachineLearningItemList<E : IMachineLearningItem> : List<E>,  IMachineLearningOutputStats
interface ClassificationItemList<E : IClassificationItem<S>, S> : MachineLearningItemList<E>,  IClassificationOutputStats<S>

interface ClassificationMetricsMap<S> {
    val liveMetrics: Map<S, StateLiveData<Triple<Int, Int, Int>?>>
    val metrics: Map<S, Triple<Int, Int, Int>>

    fun initialize (keys: Set<S>)
    fun replace (element: IClassificationOutputStats<S>)
    fun add (element: IClassificationOutputStats<S>)
    fun subtract (element: IClassificationOutputStats<S>)
    fun remove (keys: Set<S>)
    fun clear ()
}

