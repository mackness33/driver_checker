package com.example.driverchecker.machinelearning.data

// ---------------------------------- INPUT ----------------------------------

interface IMachineLearningData<Data> {
    val data: Data
}

data class MachineLearningBaseInput<Data>(
    override val data: Data,
) : IMachineLearningData<Data>


// ---------------------------------- OUTPUT ----------------------------------

interface IMachineLearningResult<Result, Superclass> {
    val result: Result
    val confidence: Float
    val group: IClassification<Superclass>
}

interface IMachineLearningResultWithInput<Data, Result, Superclass> : IMachineLearningResult<Result, Superclass>, IMachineLearningData<Data>

data class MachineLearningBaseOutput<Result, Superclass>(
    override val result: Result,
    override val confidence: Float,
    override val group: IClassification<Superclass>
) : IMachineLearningResult<Result, Superclass>

data class MachineLearningOutput<Data, Result, Superclass>(
    override val result: Result,
    override val confidence: Float,
    override val data: Data,
    override val group: IClassification<Superclass>
) : IMachineLearningResultWithInput<Data, Result, Superclass>


// ---------------------------------- TYPE ALIASES ----------------------------------

typealias MachineLearningArrayOutput<Data, Result, Superclass> = Array<IMachineLearningResultWithInput<Data, Result, Superclass>>
typealias MachineLearningArrayBaseOutput<Result, Superclass> = Array<IMachineLearningResult<Result, Superclass>>

typealias MachineLearningListOutput<Data, Result, Superclass> = List<IMachineLearningResultWithInput<Data, Result, Superclass>>
typealias MachineLearningListBaseOutput<Result, Superclass> = List<IMachineLearningResult<Result, Superclass>>

typealias MachineLearningArrayListOutput<Data, Result, Superclass> = ArrayList<IMachineLearningResultWithInput<Data, Result, Superclass>>
typealias MachineLearningArrayListBaseOutput<Result, Superclass> = ArrayList<IMachineLearningResult<Result, Superclass>>

// interface IMachineLearningMetrics<Result> : IMachineLearningResult<Result>

// data class MachineLearningWindowOutput<Data, Result>(override val result: Result, override val confidence: Float, val classes: List<Int>, val data: Data) : IMachineLearningResult<Result>