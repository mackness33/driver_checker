package com.example.driverchecker.machinelearning.manipulators

import androidx.lifecycle.LiveData
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

interface IMachineLearningClient<I, O : IMachineLearningOutput, FR : IMachineLearningFinalResult> {
    // LIVE DATA
    val hasEnded: LiveData<Boolean>

    // last result evaluated by the mlRepo
    val lastResult: LiveData<O?>

    // the index of the partialResult
    val partialResultEvent: LiveData<PartialEvaluationStateInterface>

    // array of evaluated items by the mlRepo
    val currentResultsList: List<O?>

    val lastResultsList: List<O?>

    val finalResult: ObservableData<FR?>

    val liveInput: SharedFlow<I>

    val clientState: SharedFlow<ClientStateInterface>

    val currentState: ObservableData<LiveEvaluationStateInterface?>

    fun listen (scope: CoroutineScope, evaluationFlow: SharedFlow<LiveEvaluationStateInterface>?)

    suspend fun produceInput (input: I)

    suspend fun ready ()

    suspend fun start ()

    suspend fun stop (cause: ExternalCancellationException = ExternalCancellationException())
}
