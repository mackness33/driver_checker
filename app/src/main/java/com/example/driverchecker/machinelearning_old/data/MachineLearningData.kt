package com.example.driverchecker.machinelearning_old.data

import com.example.driverchecker.machinelearning.data.IClassification
import com.example.driverchecker.machinelearning.data.IMachineLearningData

// ---------------------------------- OUTPUT ----------------------------------

interface IMachineLearningResultOld<Result, Superclass> {
    val result: Result
    val confidence: Float
    val group: IClassification<Superclass>
}

interface IMachineLearningResultWithInputOld<Data, Result, Superclass> : IMachineLearningResultOld<Result, Superclass>,
    IMachineLearningData<Data>

data class MachineLearningBaseOutputOld<Result, Superclass>(
    override val result: Result,
    override val confidence: Float,
    override val group: IClassification<Superclass>
) : IMachineLearningResultOld<Result, Superclass>

data class MachineLearningOutputOld<Data, Result, Superclass>(
    override val result: Result,
    override val confidence: Float,
    override val data: Data,
    override val group: IClassification<Superclass>
) : IMachineLearningResultWithInputOld<Data, Result, Superclass>


// ---------------------------------- TYPE ALIASES ----------------------------------

typealias MachineLearningArrayOutputOld<Data, Result, Superclass> = Array<IMachineLearningResultWithInputOld<Data, Result, Superclass>>
typealias MachineLearningArrayBaseOutputOld<Result, Superclass> = Array<IMachineLearningResultOld<Result, Superclass>>

typealias MachineLearningListOutputOld<Data, Result, Superclass> = List<IMachineLearningResultWithInputOld<Data, Result, Superclass>>
typealias MachineLearningListBaseOutputOld<Result, Superclass> = List<IMachineLearningResultOld<Result, Superclass>>

typealias MachineLearningArrayListOutputOld<Data, Result, Superclass> = ArrayList<IMachineLearningResultWithInputOld<Data, Result, Superclass>>
typealias MachineLearningArrayListBaseOutputOld<Result, Superclass> = ArrayList<IMachineLearningResultOld<Result, Superclass>>

// interface IMachineLearningMetrics<Result> : IMachineLearningResult<Result>

// data class MachineLearningWindowOutput<Data, Result>(override val result: Result, override val confidence: Float, val classes: List<Int>, val data: Data) : IMachineLearningResult<Result>