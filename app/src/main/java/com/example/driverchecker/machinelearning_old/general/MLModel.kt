package com.example.driverchecker.machinelearning_old.general

import kotlinx.coroutines.flow.*

abstract class MLModel<Data, Result>  () : MLModelInterface<Data, Result> {
    protected val _isLoaded: MutableStateFlow<Boolean> = MutableStateFlow(false)
    override val isLoaded: StateFlow<Boolean>
        get() = _isLoaded

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