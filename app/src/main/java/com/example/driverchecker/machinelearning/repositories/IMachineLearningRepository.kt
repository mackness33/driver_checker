package com.example.driverchecker.machinelearning.repositories

import com.example.driverchecker.machinelearning.data.LiveEvaluationStateInterface
import com.example.driverchecker.machinelearning.data.WithConfidence
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface IMachineLearningRepository<in I, out O : WithConfidence, FR : WithConfidence> {
    suspend fun instantEvaluation (input: I): O?

    suspend fun continuousEvaluation (input: Flow<I>, scope: CoroutineScope): O?

    fun onStartLiveEvaluation (input: SharedFlow<I>, scope: CoroutineScope)
    fun onStopLiveEvaluation (externalCause: CancellationException? = null)

    fun <ModelInit> updateModel (init: ModelInit)

    val evaluationFlowState: SharedFlow<LiveEvaluationStateInterface>?
    val repositoryScope: CoroutineScope
}