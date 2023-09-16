package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.*

interface IClassificationWindow<E : IClassificationOutputStats<S>, S> : IMachineLearningWindow<E> {
    val supergroupCounter: Map<S, Int>
//    val groupMetrics: IGroupMetrics<S>

    override fun getFinalResults() : IClassificationFinalResultStats<S>

//    override fun getFullMetrics() : Pair<IWindowMetrics, IGroupMetrics<S>>
}
