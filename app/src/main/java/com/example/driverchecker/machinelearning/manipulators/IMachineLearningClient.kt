package com.example.driverchecker.machinelearning.manipulators

import androidx.lifecycle.LiveData
import com.example.driverchecker.machinelearning.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

interface IMachineLearningClient<I, O : WithConfidence, FR : WithConfidence> {
    // LIVE DATA

    val hasEnded: LiveData<Boolean?>

    // last result evaluated by the mlRepo
    val lastResult: LiveData<O?>

    // the index of the partialResult
    val partialResultEvent: LiveData<PartialEvaluationStateInterface>

    // array of evaluated items by the mlRepo
    val currentResultsList: List<O>

    fun listen (scope: CoroutineScope, evaluationFlow: SharedFlow<LiveEvaluationStateInterface>?)

    val output: LiveData<FR?>

    suspend fun produceInput (input: I)

    val liveInput: SharedFlow<I>

    val clientState: SharedFlow<ClientStateInterface>

    suspend fun ready ()

    suspend fun start ()

    suspend fun stop (cause: ExternalCancellationException = ExternalCancellationException())
}
