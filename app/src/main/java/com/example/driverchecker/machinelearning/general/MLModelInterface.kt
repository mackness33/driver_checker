package com.example.driverchecker.machinelearning.general

interface MLModelInterface <Data, Result> {
    fun processAndEvaluate(input: Data): Result?

    fun loadModel (uri: String)
}