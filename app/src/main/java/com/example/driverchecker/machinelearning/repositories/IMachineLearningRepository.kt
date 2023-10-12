package com.example.driverchecker.machinelearning.repositories

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.utils.ObservableData
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface IMachineLearningRepository<in I, out O : IMachineLearningOutput, FR : IMachineLearningFinalResult> {
    suspend fun instantEvaluation (input: I): O?

    suspend fun continuousEvaluation (input: Flow<I>): O?

    fun onStartLiveEvaluation (input: SharedFlow<I>)
    fun onStopLiveEvaluation (externalCause: CancellationException? = null)

    fun <ModelInit> updateModel (init: ModelInit)

    val evaluationFlowState: SharedFlow<LiveEvaluationStateInterface>?
    val repositoryScope: CoroutineScope
    val availableSettings: IMultipleWindowSettingsOld
    val settings: ObservableData<ISettingsOld>

    fun updateModelThreshold (threshold: Float)
}