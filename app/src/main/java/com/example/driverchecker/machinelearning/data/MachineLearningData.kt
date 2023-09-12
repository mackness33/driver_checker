package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.utils.ISettings
import kotlinx.coroutines.flow.SharedFlow
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


// ---------------------------------- OUTPUT ----------------------------------

typealias IMachineLearningItem = WithConfidence

interface IMachineLearningOutput<I, E : IMachineLearningItem> : WithConfidence, WithInput<I> {
    val listItems: MachineLearningList<E>
}

interface IMachineLearningFinalResult : WithConfidence {
    val listOutputs: List<WithConfidence>
}

data class MachineLearningOutput <I, E : WithConfidence> (
    override val listItems: MachineLearningList<E>,
    override val input: I,
) : IMachineLearningOutput<I, E> {
    override val confidence: Float = listItems.confidence
}

data class MachineLearningFinalResult (
    override val confidence: Float,
    override val listOutputs: List<WithConfidence>
) : IMachineLearningFinalResult

typealias MachineLearningItem = MachineLearningFinalResult


// ---------------------------------- SEALED CLASSES/INTERFACES ----------------------------------

// Represents different states for the LatestNews screen
sealed interface LiveEvaluationStateInterface

// Represents different states for the LatestNews screen
sealed class LiveEvaluationState : LiveEvaluationStateInterface {
    data class Ready(val isReady: Boolean) : LiveEvaluationStateInterface
    data class Loading(val index: Int, val partialResult: WithConfidence?) : LiveEvaluationStateInterface
    object Start : LiveEvaluationStateInterface
    data class End(val exception: Throwable?, val finalResult: WithConfidence?) : LiveEvaluationStateInterface
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