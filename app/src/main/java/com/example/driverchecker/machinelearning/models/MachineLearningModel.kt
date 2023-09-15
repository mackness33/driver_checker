package com.example.driverchecker.machinelearning.models

import kotlinx.coroutines.flow.*

abstract class MachineLearningModel<I, R>  () : IMachineLearningModel<I, R> {
    protected val mIsLoaded: MutableStateFlow<Boolean> = MutableStateFlow(false)
    protected val loadingMap: Map<String, Boolean> = mutableMapOf()
    override val isLoaded: StateFlow<Boolean>
        get() = mIsLoaded
    override var threshold = 0.05f // score above which a detection is generated
        protected set

    override fun processAndEvaluate (input: I): R? {
        val data: I = preProcess(input)
        val result: R = evaluateData(data)
        return postProcess(result)
    }

    override fun processAndEvaluatesStream (input: Flow<I>): Flow<R>? {
        return input
            .buffer()
            .map { data -> preProcess(data)}
            .map { preProcessedInput -> evaluateData(preProcessedInput)}
            .map { output -> postProcess(output)}
    }

    protected abstract fun evaluateData (input: I) : R
    protected abstract fun preProcess (data: I) : I
    protected abstract fun postProcess (output: R) : R

    override fun updateThreshold (newThreshold: Float) {
        threshold = newThreshold
    }
}