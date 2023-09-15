package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.utils.ISettings
import kotlinx.coroutines.flow.SharedFlow
import kotlin.coroutines.cancellation.CancellationException
import kotlin.time.Duration

// ---------------------------------- CLASSES ----------------------------------

interface WithConfidence {
    val confidence: Float
}

interface WithSettings {
    val settings: ISettings?
}

interface WithMetrics {
    val metrics: IMetrics?
}

interface IMetrics {
    val totalTime: Duration
    val totalWindows: Int
}


data class MachineLearningMetrics (
    override val totalTime: Duration,
    override val totalWindows: Int
) : IMetrics

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

typealias IMachineLearningOutputStats = WithConfidence

interface IMachineLearningOutput<E : IMachineLearningItem> : IMachineLearningOutputStats {
    val listItems: MachineLearningList<E>
}

typealias IMachineLearningFinalResultStats = WithConfidence

interface IMachineLearningFinalResult : IMachineLearningFinalResultStats, WithMetrics, WithSettings

data class MachineLearningOutput <E : WithConfidence> (
    override val listItems: MachineLearningList<E>,
) : IMachineLearningOutput<E> {
    override val confidence: Float = listItems.confidence
}

data class MachineLearningFinalResult (
    override val confidence: Float,
    override val settings: ISettings? = null,
    override val metrics: IMetrics? = null,
) : IMachineLearningFinalResult {

    constructor(main: IMachineLearningFinalResultStats, settings: ISettings?, metrics: IMetrics?) : this (
        main.confidence, settings, metrics
    )
}

data class MachineLearningItem (
    override val confidence: Float
) : IMachineLearningItem


// ---------------------------------- FULL OUTPUT ----------------------------------

typealias IMachineLearningFullItem = IMachineLearningItem

interface IMachineLearningFullOutput<I, E : IMachineLearningFullItem> : IMachineLearningOutput<E>, WithInput<I>

interface IMachineLearningFullFinalResult : IMachineLearningFinalResult

data class MachineLearningFullOutput <I, E : WithConfidence> (
    override val listItems: MachineLearningList<E>,
    override val input: I,
) : IMachineLearningFullOutput<I, E> {
    override val confidence: Float = listItems.confidence
}

data class MachineLearningFullFinalResult (
    override val confidence: Float,
    override val settings: ISettings? = null,
    override val metrics: IMetrics? = null
) : IMachineLearningFullFinalResult

typealias MachineLearningFullItem = IMachineLearningFullItem


// ---------------------------------- SEALED CLASSES/INTERFACES ----------------------------------

// Represents different states for the LatestNews screen
sealed interface LiveEvaluationStateInterface

// Represents different states for the LatestNews screen
sealed class LiveEvaluationState : LiveEvaluationStateInterface {
    data class Ready(val isReady: Boolean) : LiveEvaluationStateInterface
    data class Loading(val index: Int, val partialResult: IMachineLearningOutputStats?) : LiveEvaluationStateInterface
    object Start : LiveEvaluationStateInterface
    data class End(val exception: Throwable?, val finalResult: IMachineLearningFinalResult?) : LiveEvaluationStateInterface
}

// Represents different states for the LatestNews screen
sealed interface PartialEvaluationStateInterface

// Represents different states for the LatestNews screen
sealed class PartialEvaluationState : PartialEvaluationStateInterface {
    data class Insert(val index: Int) : PartialEvaluationState()
    object Clear : PartialEvaluationState()
    object Init : PartialEvaluationState()
}



// Represents different states for the LatestNews screen
sealed interface ClientStateInterface

// Represents different states for the LatestNews screen
sealed class ClientState : ClientStateInterface {
    object Ready : ClientState()
    data class Start<E>(val input: SharedFlow<E>, val settings: ISettings) : ClientState()
    data class Stop(val cause: ExternalCancellationException) : ClientState()
}


// ---------------------------------- ERRORS ----------------------------------


class ExternalCancellationException : CancellationException ()
class InternalCancellationException : CancellationException ()
class CorrectCancellationException : CancellationException ()