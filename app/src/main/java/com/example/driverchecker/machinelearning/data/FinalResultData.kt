package com.example.driverchecker.machinelearning.data

interface IMachineLearningFinalResultStats : WithConfidence, IStats
data class MachineLearningFinalResultStats(
    override val confidence: Float
) : IMachineLearningFinalResultStats

interface IMachineLearningFinalResultsMetrics : IMetrics, WithWindowData
data class MachineLearningFinalResultsMetrics(
    override val data: Map<IWindowBasicData, IAdditionalMetrics?>
) : IMachineLearningFinalResultsMetrics

interface IMachineLearningFinalResult {
    val stats: IMachineLearningFinalResultStats
    val metrics: IMachineLearningFinalResultsMetrics?
}

data class MachineLearningFinalResult (
    override val stats: IMachineLearningFinalResultStats,
    override val metrics: IMachineLearningFinalResultsMetrics?,
) : IMachineLearningFinalResult {
    constructor(copy: IMachineLearningFinalResult) : this (
        copy.stats, copy.metrics
    )
}


interface IClassificationFinalResultStats<G> : IMachineLearningFinalResultStats, WithSupergroup<G>
data class ClassificationFinalResultStats<G>(
    override val confidence: Float
) : IMachineLearningFinalResultStats

interface IClassificationFinalResultMetrics<G> : WithGroupsData<G>, IMachineLearningFinalResultsMetrics
data class ClassificationFinalResultMetrics<G>(
    override val data: Map<IWindowBasicData, IGroupMetrics<G>?>
) : IClassificationFinalResultMetrics<G>


interface IClassificationFinalResult<G> : IMachineLearningFinalResult {
    override val stats: IClassificationFinalResultStats<G>
    override val metrics: IClassificationFinalResultMetrics<G>?
}

data class ClassificationFinalResult<G> (
    override val stats: IClassificationFinalResultStats<G>,
    override val metrics: IClassificationFinalResultMetrics<G>?,
) : IClassificationFinalResult<G> {
    constructor(copy: IClassificationFinalResult<G>) : this (
        copy.stats, copy.metrics
    )
}