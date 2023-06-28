package com.example.driverchecker.machinelearning.general

import com.example.driverchecker.machinelearning.data.LiveEvaluationStateInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface IMachineLearningRepository<in Data, out Result> {
    suspend fun instantClassification (input: Data): Result?

    suspend fun continuousClassification (input: List<Data>): Result?
    suspend fun continuousClassification (input: Flow<Data>, scope: CoroutineScope): Result?

    fun onStartLiveClassification (input: SharedFlow<Data>, scope: CoroutineScope)
    fun onStopLiveClassification ()

    fun <ModelInit> updateModel (init: ModelInit)

    val analysisProgressState: SharedFlow<LiveEvaluationStateInterface<Result>>?
    val repositoryScope: CoroutineScope
}