package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.machinelearning.collections.MachineLearningItemListOld

interface IStats

interface IMetrics

interface IMutableOutput<E : IMachineLearningItem> {
    fun push(item: E): Boolean
    fun getImmutable() : IMachineLearningOutput<E>
}


interface IMachineLearningOutputStats : WithConfidence, IStats
data class MachineLearningStats(override val confidence: Float) : IMachineLearningOutputStats


interface IMachineLearningOutput<E : IMachineLearningItem> {
    val items: List<E>
    val stats: IMachineLearningOutputStats
    val metrics: IMetrics?
}

data class MachineLearningOutput<E : IMachineLearningItem> (
    override val items: List<E>,
    override val stats: IMachineLearningOutputStats,
    override val metrics: IMetrics?,
) : IMachineLearningOutput<E> {
    constructor(copy: IMachineLearningOutput<E>) : this (
        copy.items, copy.stats, copy.metrics
    )
}


interface IMutableClassificationOutput<E : IClassificationItem<G>, G> : IMutableOutput<E> {
    override fun getImmutable() : IClassificationOutput<E, G>
}

interface IClassificationOutputStats<G> : IMachineLearningOutputStats, WithGroups<G>
data class ClassificationStats<G>(
    override val confidence: Float,
    override val groups: Map<G, Set<IClassificationWithMetrics<G>>>
) : IClassificationOutputStats<G>

interface IClassificationOutput<E : IClassificationItem<G>, G> : IMachineLearningOutput<E> {
    override val stats: IClassificationOutputStats<G>
}

data class ClassificationOutput<E : IClassificationItem<G>, G> (
    override val items: List<E>,
    override val stats: IClassificationOutputStats<G>,
    override val metrics: IMetrics?,
) : IClassificationOutput<E, G> {
    constructor(copy: IClassificationOutput<E, G>) : this (
        copy.items, copy.stats, copy.metrics
    )
}