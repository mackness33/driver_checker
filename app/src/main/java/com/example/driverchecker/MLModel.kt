package com.example.driverchecker

abstract class MLModel<T> {
    protected var isLoaded: Boolean = false

    fun analyzeData (data: T) : String {
        if (!isLoaded) {
            return "The module has not been loaded"
        }
        val preData: T = preProcess(data)
        return evaluateData(preData)
//        postProcess(data)
    }
    protected abstract fun evaluateData (data: T) : String
    protected abstract fun preProcess (data: T) : T
//    protected abstract fun postProcess (data: T) : T
}