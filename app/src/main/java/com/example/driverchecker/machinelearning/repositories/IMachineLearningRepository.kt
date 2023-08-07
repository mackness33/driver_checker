package com.example.driverchecker.machinelearning.repositories

import com.example.driverchecker.machinelearning.data.LiveEvaluationStateInterface
import com.example.driverchecker.machinelearning.data.WithConfidence
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface IMachineLearningRepository<in Data, out Result : WithConfidence> {
    suspend fun instantEvaluation (input: Data): Result?

    suspend fun continuousEvaluation (input: Flow<Data>, scope: CoroutineScope): Result?

    fun onStartLiveEvaluation (input: SharedFlow<Data>, scope: CoroutineScope)
    fun onStopLiveEvaluation (externalCause: CancellationException? = null)

    fun <ModelInit> updateModel (init: ModelInit)

    val analysisProgressState: SharedFlow<LiveEvaluationStateInterface>?
    val repositoryScope: CoroutineScope
}