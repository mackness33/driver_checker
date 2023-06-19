package com.example.driverchecker.machinelearning.data

interface IMachineLearningResult<Result> {
    val result: Result
    val confidence: Float
    val classes: List<Int>
}

interface IMachineLearningResultWithInput<Data, Result> : IMachineLearningResult<Result> {
    val data: Data
}

interface IMachineLearningMetrics<Result> : IMachineLearningResult<Result> {}

data class MachineLearningBaseOutput<Result>(
    override val result: Result,
    override val confidence: Float,
    override val classes: List<Int>
) : IMachineLearningResult<Result>

data class MachineLearningOutput<Data, Result>(
    override val result: Result,
    override val confidence: Float,
    override val data: Data,
    override val classes: List<Int>
) : IMachineLearningResultWithInput<Data, Result>

// data class MachineLearningWindowOutput<Data, Result>(override val result: Result, override val confidence: Float, val classes: List<Int>, val data: Data) : IMachineLearningResult<Result>