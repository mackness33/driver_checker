package com.example.driverchecker.machinelearning.data

// ---------------------------------- INPUT ----------------------------------

interface IMachineLearningData<Data> {
    val data: Data
}

data class MachineLearningBaseInput<Data>(
    override val data: Data,
) : IMachineLearningData<Data>


// ---------------------------------- OUTPUT ----------------------------------

interface IMachineLearningResult<Result> {
    val result: Result
    val confidence: Float
    val classes: List<Int>
}

interface IMachineLearningResultWithInput<Data, Result> : IMachineLearningResult<Result>, IMachineLearningData<Data>

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


// ---------------------------------- TYPE ALIASES ----------------------------------

typealias MachineLearningArrayOutput<Data, Result> = Array<IMachineLearningResultWithInput<Data, Result>>
typealias MachineLearningArrayBaseOutput<Result> = Array<IMachineLearningResult<Result>>

typealias MachineLearningListOutput<Data, Result> = List<IMachineLearningResultWithInput<Data, Result>>
typealias MachineLearningListBaseOutput<Result> = List<IMachineLearningResult<Result>>

typealias MachineLearningArrayListOutput<Data, Result> = ArrayList<IMachineLearningResultWithInput<Data, Result>>
typealias MachineLearningArrayListBaseOutput<Result> = ArrayList<IMachineLearningResult<Result>>

// interface IMachineLearningMetrics<Result> : IMachineLearningResult<Result>

// data class MachineLearningWindowOutput<Data, Result>(override val result: Result, override val confidence: Float, val classes: List<Int>, val data: Data) : IMachineLearningResult<Result>