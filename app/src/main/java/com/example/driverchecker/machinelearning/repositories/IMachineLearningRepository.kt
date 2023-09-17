package com.example.driverchecker.machinelearning.repositories

import com.example.driverchecker.machinelearning.data.IMachineLearningFinalResult
import com.example.driverchecker.machinelearning.data.IMachineLearningOutputStats
import com.example.driverchecker.machinelearning.data.ISettings
import com.example.driverchecker.machinelearning.data.LiveEvaluationStateInterface
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface IMachineLearningRepository<in I, out O : IMachineLearningOutputStats, FR : IMachineLearningFinalResult> {
    suspend fun instantEvaluation (input: I): O?

    suspend fun continuousEvaluation (input: Flow<I>, settings: ISettings): O?

    fun onStartLiveEvaluation (input: SharedFlow<I>)
    fun onStopLiveEvaluation (externalCause: CancellationException? = null)

    fun <ModelInit> updateModel (init: ModelInit)

    val evaluationFlowState: SharedFlow<LiveEvaluationStateInterface>?
    val repositoryScope: CoroutineScope
}