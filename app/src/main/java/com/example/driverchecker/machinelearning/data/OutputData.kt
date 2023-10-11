package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.machinelearning.collections.MachineLearningItemListOld

interface IStats

interface IMetrics

interface IMachineLearningOutputStats : WithConfidence, IStats

interface IMachineLearningOutputMetrics : IMetrics

interface IMachineLearningOutput<E : IMachineLearningItem> {
    val items: List<E>
    val stats: IMachineLearningOutputStats
    val metrics: IMachineLearningOutputMetrics?
}

data class MachineLearningOutput<E : IMachineLearningItem> (
    override val items: List<E>,
    override val stats: IMachineLearningOutputStats,
    override val metrics: IMachineLearningOutputMetrics?,
) : IMachineLearningOutput<E>

data class MachineLearningStats(override val confidence: Float) : IMachineLearningOutputStats