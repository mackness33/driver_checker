package com.example.driverchecker.machinelearning.general

abstract class MLModel<Data, Result>  (private val modelPath: String? = null) : MLModelInterface<Data, Result> {
    var isLoaded: Boolean = false
        protected set

    init {
        if (modelPath != null)
            loadModel(modelPath)
    }

    override fun processAndEvaluate (input: Data): Result? {
        val data: Data = preProcess(input)
        val result: Result = evaluateData(data)
        return postProcess(result)
    }

    protected abstract fun evaluateData (input: Data) : Result
    protected abstract fun preProcess (data: Data) : Data
    protected abstract fun postProcess (output: Result) : Result
}