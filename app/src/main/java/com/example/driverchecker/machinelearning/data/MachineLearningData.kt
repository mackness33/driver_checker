package com.example.driverchecker.machinelearning.data

// ---------------------------------- INPUT ----------------------------------

interface IMachineLearningData<Data> {
    val data: Data
}

data class MachineLearningBaseInput<Data>(
    override val data: Data,
) : IMachineLearningData<Data>


// ---------------------------------- OUTPUT ----------------------------------

interface IMachineLearningResult<Result> : WithConfidence{
    val result: Result
}

interface IMachineLearningResultWithInput<Data, Result> : IMachineLearningResult<Result>,
    IMachineLearningData<Data>

data class MachineLearningBaseOutput<Result>(
    override val result: Result,
    override val confidence: Float
) : IMachineLearningResult<Result>

data class MachineLearningOutput<Data, Result>(
    override val result: Result,
    override val confidence: Float,
    override val data: Data,
) : IMachineLearningResultWithInput<Data, Result>


// ---------------------------------- TYPE ALIASES ----------------------------------

typealias MachineLearningListOutput<Data, Result> = MachineLearningResultList<IMachineLearningResultWithInput<Data, Result>>
typealias MachineLearningListBaseOutput<Result> = MachineLearningResultList<IMachineLearningResult<Result>>

typealias MachineLearningArrayListOutput<Data, Result> = MachineLearningResultArrayList<IMachineLearningResultWithInput<Data, Result>>
typealias MachineLearningArrayListBaseOutput<Result> = MachineLearningResultArrayList<IMachineLearningResult<Result>>

// interface IMachineLearningMetrics<Result> : IMachineLearningResult<Result>

// data class MachineLearningWindowOutput<Data, Result>(override val result: Result, override val confidence: Float, val classes: List<Int>, val data: Data) : IMachineLearningResult<Result>


// Represents different states for the LatestNews screen
sealed interface LiveEvaluationStateInterface<out Result>

// Represents different states for the LatestNews screen
sealed class LiveEvaluationState<out Result> : LiveEvaluationStateInterface<Result> {
    data class Ready(val isReady: Boolean) : LiveEvaluationState<Nothing>()
    data class Loading<Result>(val index: Int, val partialResult: Result?) : LiveEvaluationState<Result>()
    class Start() : LiveEvaluationState<Nothing>()
    data class End<Result>(val exception: Throwable?, val result: Result?) : LiveEvaluationState<Result>()
}

// Represents different states for the LatestNews screen
sealed class LiveClassificationState<out Result> : LiveEvaluationState<Result>() {
    data class Start(val maxClassesPerGroup: Int) : LiveEvaluationState<Nothing>()
}