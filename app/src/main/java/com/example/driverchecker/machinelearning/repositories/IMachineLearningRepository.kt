package com.example.driverchecker.machinelearning.repositories

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.utils.ObservableData
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface IMachineLearningRepository<in I, out O : IMachineLearningOutputStats, FR : IMachineLearningFinalResult> {
    suspend fun instantEvaluation (input: I): O?

    suspend fun continuousEvaluation (input: Flow<I>, settings: IOldSettings): O?

    fun onStartLiveEvaluation (input: SharedFlow<I>)
    fun onStopLiveEvaluation (externalCause: CancellationException? = null)

    fun <ModelInit> updateModel (init: ModelInit)

    val evaluationFlowState: SharedFlow<LiveEvaluationStateInterface>?
    val repositoryScope: CoroutineScope
    val availableSettings: IMultipleWindowSettings
    val settings: ObservableData<ISettings>
}