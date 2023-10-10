package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.machinelearning.collections.MachineLearningItemList
import kotlin.coroutines.cancellation.CancellationException

// ---------------------------------- MACHINE LEARNING ----------------------------------
typealias IMachineLearningFinalResultStats = WithConfidence

interface IMachineLearningNewFinalResult : IMachineLearningFinalResultStats, WithWindowInfo

data class MachineLearningNewFinalResult (
    override val confidence: Float, override val data: Map<IWindowBasicData, IAdditionalMetrics?>,
) : IMachineLearningFinalResult {
    constructor(main: IMachineLearningFinalResultStats, data: Map<IWindowBasicData, IAdditionalMetrics?>) : this (
        main.confidence, data
    )
}


interface IMachineLearningFinalResult : IMachineLearningFinalResultStats, WithWindowData

data class MachineLearningFinalResult (
    override val confidence: Float, override val data: Map<IWindowBasicData, IAdditionalMetrics?>,
) : IMachineLearningFinalResult {
    constructor(main: IMachineLearningFinalResultStats, data: Map<IWindowBasicData, IAdditionalMetrics?>) : this (
        main.confidence, data
    )
}

interface IOldMachineLearningFinalResult : IMachineLearningFinalResultStats, WithOldMetrics, WithOldSettings

data class OldMachineLearningFinalResult (
    override val confidence: Float,
    override val settings: IOldSettings? = null,
    override val metrics: IOldMetrics? = null,
) : IOldMachineLearningFinalResult {

    constructor(main: IMachineLearningFinalResultStats, settings: IOldSettings?, metrics: IOldMetrics?) : this (
        main.confidence, settings, metrics
    )
}

// ---------------------------------- FULL OUTPUT ----------------------------------
interface IOldMachineLearningFullFinalResult : IOldMachineLearningFinalResult

data class OldMachineLearningFullFinalResult (
    override val confidence: Float,
    override val settings: IOldSettings? = null,
    override val metrics: IOldMetrics? = null
) : IOldMachineLearningFullFinalResult



// ---------------------------------- ERRORS ----------------------------------


class ExternalCancellationException : CancellationException ()
class InternalCancellationException : CancellationException ()
class CorrectCancellationException : CancellationException ()