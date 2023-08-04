package com.example.driverchecker.machinelearning.data

import kotlin.coroutines.cancellation.CancellationException

// ---------------------------------- CLASSES ----------------------------------

interface WithConfidence {
    val confidence: Float
}

interface IMachineLearningFinalResult : WithConfidence

data class MachineLearningFinalResult (
    override val confidence: Float
) : IMachineLearningFinalResult

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

interface IMachineLearningOutput<D, R : WithConfidence> : IMachineLearningFinalResult {
    val listPartialResults: MachineLearningResultList<IMachineLearningResult<D, R>>
}

data class MachineLearningResult <D, R : WithConfidence> (
    override val listItems: MachineLearningResultList<R>,
    override val data: D,
) : IMachineLearningResult<D, R> {
    override val confidence: Float = listItems.confidence
}

data class MachineLearningOutput <D, R: WithConfidence> (
    override val listPartialResults: MachineLearningResultList<IMachineLearningResult<D, R>>,
    override val confidence: Float
) : IMachineLearningOutput<D, R>


// ---------------------------------- SEALED CLASSES/INTERFACES ----------------------------------

// Represents different states for the LatestNews screen
sealed interface LiveEvaluationStateInterface<out Result>

// Represents different states for the LatestNews screen
sealed class LiveEvaluationState<out Result> : LiveEvaluationStateInterface<Result> {
    data class Ready(val isReady: Boolean) : LiveEvaluationState<Nothing>()
    data class Loading<Result>(val index: Int, val partialResult: Result?) : LiveEvaluationState<Result>()
    object Start : LiveEvaluationState<Nothing>()
    data class End<Result>(val exception: Throwable?, val result: Result?) : LiveEvaluationState<Result>()
}

// Represents different states for the LatestNews screen
sealed class LiveClassificationState<out Result> : LiveEvaluationState<Result>() {
    data class Start(val maxClassesPerGroup: Int) : LiveEvaluationState<Nothing>()
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