package com.example.driverchecker.machinelearning.general

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.map

abstract class MLModel<Data, Result>  () : MLModelInterface<Data, Result> {
    var isLoaded: Boolean = false
        protected set

    override fun processAndEvaluate (input: Data): Result? {
        val data: Data = preProcess(input)
        val result: Result = evaluateData(data)
        return postProcess(result)
    }

    override fun processAndEvaluatesStream (input: Flow<Data>): Flow<Result>? {
        return input
            .buffer()
            .map { data -> preProcess(data)}
            .map { preProcessedInput -> evaluateData(preProcessedInput)}
            .map { output -> postProcess(output)}
    }

    protected abstract fun evaluateData (input: Data) : Result
    protected abstract fun preProcess (data: Data) : Data
    protected abstract fun postProcess (output: Result) : Result
}