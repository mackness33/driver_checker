package com.example.driverchecker.machinelearning.data

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
    override val data: I,
) : IMachineLearningData<I>


// ---------------------------------- OUTPUT ----------------------------------

typealias IMachineLearningItem = WithConfidence

interface IMachineLearningOutput<I, E : IMachineLearningItem> : WithConfidence, WithInput<I> {
    val listItems: MachineLearningResultList<E>
}

typealias IMachineLearningFinalResult = WithConfidence

data class MachineLearningOutput <I, E : WithConfidence> (
    override val listItems: MachineLearningResultList<E>,
    override val input: I,
) : IMachineLearningOutput<I, E> {
    override val confidence: Float = listItems.confidence
}

data class MachineLearningFinalResult (
    override val confidence: Float
) : WithConfidence

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


// ---------------------------------- ERRORS ----------------------------------


class ExternalCancellationException : CancellationException ()
class CorrectCancellationException : CancellationException ()



// ------------- OLD --------

interface IMachineLearningResult<D, R : WithConfidence> : WithConfidence {
    val data: D
    val listItems: MachineLearningResultList<R>
}

interface IMachineLearningOutputOld<D, R : WithConfidence> : WithConfidence {
    val listPartialResults: MachineLearningResultList<R>
}

data class MachineLearningResultOld <D, R : WithConfidence> (
    override val listItems: MachineLearningResultList<R>,
    override val data: D,
) : IMachineLearningResult<D, R> {
    override val confidence: Float = listItems.confidence
}

data class MachineLearningOutputOld <D, R: WithConfidence> (
    override val listPartialResults: MachineLearningResultList<R>,
    override val confidence: Float
) : IMachineLearningOutputOld<D, R>



interface IMachineLearningData<D> {
    val data: D
}