package com.example.driverchecker.machinelearning.data

// ---------------------------------- CLASSES ----------------------------------

interface WithConfidence {
    val confidence: Float
}

// ---------------------------------- INPUT ----------------------------------

interface IMachineLearningInput<D> {
    val data: D
}

data class MachineLearningInput<D>(
    override val data: D,
) : IMachineLearningInput<D>


// ---------------------------------- OUTPUT ----------------------------------

interface IMachineLearningBasicItem<R> : WithConfidence {
    val result: R
}

interface IMachineLearningResult<D, R : WithConfidence> : WithConfidence {
    val data: D
    val listItems: MachineLearningResultList<R>
}

interface IMachineLearningOutput<D, R : WithConfidence> {
    val listPartialResults: MachineLearningResultList<IMachineLearningResult<D, R>>
}

interface IMachineLearningBasicItemWithInput<Data, Result> : IMachineLearningBasicItem<Result>,
    IMachineLearningInput<Data>

data class MachineLearningResult <D, R : WithConfidence> (
    override val listItems: MachineLearningResultList<R>,
    override val data: D,
) : IMachineLearningResult<D, R> {
    override val confidence: Float = listItems.confidence
}

data class MachineLearningOutput <D, R: WithConfidence> (
    override val listPartialResults: MachineLearningResultList<IMachineLearningResult<D, R>>
) : IMachineLearningOutput<D, R>


// ---------------------------------- TYPE ALIASES ----------------------------------

typealias MachineLearningListOutput<Data, Result> = MachineLearningResultList<IMachineLearningBasicItemWithInput<Data, Result>>
typealias MachineLearningListBaseOutput<Result> = MachineLearningResultList<IMachineLearningBasicItem<Result>>

typealias MachineLearningArrayListOutput<Data, Result> = MachineLearningResultArrayList<IMachineLearningBasicItemWithInput<Data, Result>>
typealias MachineLearningArrayListBaseOutput<Result> = MachineLearningResultArrayList<IMachineLearningBasicItem<Result>>

// interface IMachineLearningMetrics<Result> : IMachineLearningResult<Result>

// data class MachineLearningWindowOutput<Data, Result>(override val result: Result, override val confidence: Float, val classes: List<Int>, val data: Data) : IMachineLearningResult<Result>


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