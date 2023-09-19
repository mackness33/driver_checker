package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.machinelearning.collections.MachineLearningItemList
import kotlin.coroutines.cancellation.CancellationException

// ---------------------------------- CLASSES ----------------------------------

interface WithConfidence {
    val confidence: Float
}


// ---------------------------------- INPUT ----------------------------------

interface WithInput<I> {
    val input: I
}

typealias IMachineLearningInput<I> = WithInput<I>

data class MachineLearningInput<I>(
    override val input: I,
) : IMachineLearningInput<I>

// ---------------------------------- BASIC OUTPUT ----------------------------------
typealias IMachineLearningItem = WithConfidence

interface IMachineLearningOutputStats : WithConfidence {
    val time: Double?
    fun updateTime(newTime: Double?)
}

interface IMachineLearningOutput<E : IMachineLearningItem> : IMachineLearningOutputStats {
    val listItems: MachineLearningItemList<E>
}

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

data class MachineLearningOutput <E : WithConfidence> (
    override val listItems: MachineLearningItemList<E>,
) : IMachineLearningOutput<E> {
    override val confidence: Float = listItems.confidence
    override var time: Double? = null
        private set

    override fun updateTime(newTime: Double?) {
        time = newTime
    }
}

data class OldMachineLearningFinalResult (
    override val confidence: Float,
    override val settings: IOldSettings? = null,
    override val metrics: IOldMetrics? = null,
) : IOldMachineLearningFinalResult {

    constructor(main: IMachineLearningFinalResultStats, settings: IOldSettings?, metrics: IOldMetrics?) : this (
        main.confidence, settings, metrics
    )
}

data class MachineLearningItem (
    override val confidence: Float
) : IMachineLearningItem


// ---------------------------------- FULL OUTPUT ----------------------------------

typealias IMachineLearningFullItem = IMachineLearningItem

interface IMachineLearningFullOutput<I, E : IMachineLearningFullItem> : IMachineLearningOutput<E>, WithInput<I>

interface IOldMachineLearningFullFinalResult : IOldMachineLearningFinalResult

data class MachineLearningFullOutput <I, E : WithConfidence> (
    override val listItems: MachineLearningItemList<E>,
    override val input: I,
) : IMachineLearningFullOutput<I, E> {
    override val confidence: Float = listItems.confidence
    override var time: Double? = null
        private set

    override fun updateTime(newTime: Double?) {
        time = newTime
    }
}

data class OldMachineLearningFullFinalResult (
    override val confidence: Float,
    override val settings: IOldSettings? = null,
    override val metrics: IOldMetrics? = null
) : IOldMachineLearningFullFinalResult

typealias MachineLearningFullItem = IMachineLearningFullItem




// ---------------------------------- ERRORS ----------------------------------


class ExternalCancellationException : CancellationException ()
class InternalCancellationException : CancellationException ()
class CorrectCancellationException : CancellationException ()