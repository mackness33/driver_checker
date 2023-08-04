package com.example.driverchecker.machinelearning.repositories

import com.example.driverchecker.machinelearning.data.LiveEvaluationStateInterface
import com.example.driverchecker.machinelearning.data.WithConfidence
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface IMachineLearningRepository<in Data, out Result : WithConfidence> {
    suspend fun instantClassification (input: Data): Result?

    suspend fun continuousClassification (input: Flow<Data>, scope: CoroutineScope): Result?

    fun onStartLiveClassification (input: SharedFlow<Data>, scope: CoroutineScope)
    fun onStopLiveClassification (externalCause: CancellationException? = null)

    fun <ModelInit> updateModel (init: ModelInit)

    val analysisProgressState: SharedFlow<LiveEvaluationStateInterface<Result>>?
    val repositoryScope: CoroutineScope
}