package com.example.driverchecker.machinelearning.general

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface MLRepositoryInterface<in Data, out Result> {
    suspend fun instantClassification (input: Data): Result?

    suspend fun continuousClassification (input: List<Data>): Result?
    suspend fun continuousClassification (input: Flow<Data>, scope: CoroutineScope): Result?

    suspend fun onStartLiveClassification (input: SharedFlow<Data>, scope: CoroutineScope)
    suspend fun onStopLiveClassification ()

//    fun liveClassification (input: Flow<Data>): Result?
}