package com.example.driverchecker.machinelearning.general

import kotlinx.coroutines.flow.Flow

interface MLModelInterface <Data, Result> {
    fun processAndEvaluate(input: Data): Result?

    fun processAndEvaluatesStream (input: Flow<Data>): Flow<Result>?

    fun loadModel (uri: String)
}