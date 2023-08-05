package com.example.driverchecker.machinelearning.data

import kotlin.coroutines.cancellation.CancellationException

// ---------------------------------- CLASSES ----------------------------------

interface WithConfidence {
    val confidence: Float
}

data class MachineLearningFinalResult (
    override val confidence: Float
) : WithConfidence

// ---------------------------------- INPUT ----------------------------------

interface IMachineLearningInput<D> {
    val data: D
}
// ---------------------------------- SEALED CLASSES/INTERFACES ----------------------------------

data class MachineLearningInput<D>(
    override val data: D,
) : IMachineLearningInput<D>


// ---------------------------------- OUTPUT ----------------------------------

interface IMachineLearningResult<D, R : WithConfidence> : WithConfidence {
    val data: D
    val listItems: MachineLearningResultList<R>
}

interface IMachineLearningOutput<D, R : WithConfidence> : WithConfidence {
    val listPartialResults: MachineLearningResultList<R>
}

data class MachineLearningResult <D, R : WithConfidence> (
    override val listItems: MachineLearningResultList<R>,
    override val data: D,
) : IMachineLearningResult<D, R> {
    override val confidence: Float = listItems.confidence
}

data class MachineLearningOutput <D, R: WithConfidence> (
    override val listPartialResults: MachineLearningResultList<R>,
    override val confidence: Float
) : IMachineLearningOutput<D, R>


// ---------------------------------- SEALED CLASSES/INTERFACES ----------------------------------

// Represents different states for the LatestNews screen
sealed interface LiveEvaluationStateInterface

// Represents different states for the LatestNews screen
sealed class LiveEvaluationState : LiveEvaluationStateInterface {
    data class Ready(val isReady: Boolean) : LiveEvaluationState()
    data class Loading<R>(val index: Int, val partialResult: R?) : LiveEvaluationState()
    object Start : LiveEvaluationState()
    data class End(val exception: Throwable?, val finalResult: WithConfidence?) : LiveEvaluationState()
}


// Represents different states for the LatestNews screen
sealed interface PartialEvaluationStateInterface

// Represents different states for the LatestNews screen
sealed class PartialEvaluationState : PartialEvaluationStateInterface {
    data class Insert(val index: Int) : PartialEvaluationState()
    object Clear : PartialEvaluationState()
    object Init : PartialEvaluationState()
}


// ---------------------------------- ERRORS ----------------------------------


class ExternalCancellationException : CancellationException ()
class CorrectCancellationException : CancellationException ()