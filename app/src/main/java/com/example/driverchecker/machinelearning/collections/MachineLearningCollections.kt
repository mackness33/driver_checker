package com.example.driverchecker.machinelearning.collections

import com.example.driverchecker.machinelearning.data.IClassificationItem
import com.example.driverchecker.machinelearning.data.IClassificationOutputStats
import com.example.driverchecker.machinelearning.data.IMachineLearningItem
import com.example.driverchecker.machinelearning.data.IMachineLearningOutputStats
import com.example.driverchecker.utils.StateLiveData

interface MachineLearningItemList<E : IMachineLearningItem> : List<E>, IMachineLearningOutputStats
interface ClassificationItemList<E : IClassificationItem<S>, S> : MachineLearningItemList<E>,
    IClassificationOutputStats<S>

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

