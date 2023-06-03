package com.example.driverchecker.machinelearning.general

import com.example.driverchecker.machinelearning.general.local.LiveEvaluationStateInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface MLRepositoryInterface<in Data, out Result> {
    suspend fun instantClassification (input: Data): Result?

    suspend fun continuousClassification (input: List<Data>): Result?
    suspend fun continuousClassification (input: Flow<Data>, scope: CoroutineScope): Result?

    suspend fun onStartLiveClassification (input: SharedFlow<Data>, scope: CoroutineScope)
    suspend fun onStopLiveClassification ()

    val analysisProgressState: StateFlow<LiveEvaluationStateInterface<Result>>?
    val repositoryScope: CoroutineScope
//    fun liveClassification (input: Flow<Data>): Result?
}