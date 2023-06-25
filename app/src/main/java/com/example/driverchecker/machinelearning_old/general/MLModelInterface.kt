package com.example.driverchecker.machinelearning_old.general

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow

interface MLModelInterface <Data, Result> {
    fun processAndEvaluate(input: Data): Result?

    fun processAndEvaluatesStream (input: Flow<Data>): Flow<Result>?

    fun loadModel (uri: String)

    val isLoaded: StateFlow<Boolean>
}