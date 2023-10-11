package com.example.driverchecker.machinelearning.data

interface IStats

interface IMetrics

interface IMachineLearningMutableOutput<E : IMachineLearningItem> : IMachineLearningOutput {
    fun push(item: E): Boolean
    fun getImmutable() : IMachineLearningOutput
}


interface IMachineLearningOutputStats : WithConfidence, IStats
data class MachineLearningStats(override val confidence: Float) : IMachineLearningOutputStats


interface IMachineLearningOutput : WithIndex {
    val items: List<IMachineLearningItem>
    val stats: IMachineLearningOutputStats
    val metrics: IMetrics?
}

data class MachineLearningOutput (
    override val items: List<IMachineLearningItem>,
    override val stats: IMachineLearningOutputStats,
    override val metrics: IMetrics?,
    override val index: Int,
) : IMachineLearningOutput {
    constructor(copy: IMachineLearningOutput) : this (
        copy.items, copy.stats, copy.metrics, copy.index
    )
}


interface IMutableClassificationOutput<E : IClassificationItem<G>, G> : IMachineLearningMutableOutput<E>, IClassificationOutput<G> {
    override fun getImmutable() : IClassificationOutput<G>
}

interface IClassificationOutputStats<G> : IMachineLearningOutputStats, WithGroups<G>
data class ClassificationStats<G>(
    override val confidence: Float,
    override val groups: Map<G, Set<IClassificationWithMetrics<G>>>
) : IClassificationOutputStats<G>

interface IClassificationOutput<G> : IMachineLearningOutput {
    override val stats: IClassificationOutputStats<G>
    override val items: List<IClassificationItem<G>>
}

data class ClassificationOutput<G> (
    override val items: List<IClassificationItem<G>>,
    override val stats: IClassificationOutputStats<G>,
    override val metrics: IMetrics?,
    override val index: Int,
) : IClassificationOutput<G> {
    constructor(copy: IClassificationOutput<G>) : this (
        copy.items, copy.stats, copy.metrics, copy.index
    )
}