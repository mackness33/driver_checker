package com.example.driverchecker.machinelearning.manipulators

import androidx.lifecycle.LiveData
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

interface IClient {
    val currentState: ObservableData<LiveEvaluationStateInterface?>
    val clientState: SharedFlow<ClientStateInterface>
}


interface ILiveClient<I, O> : IClient {
    // To check if the client has receive the last loading and the final
    val hasEnded: LiveData<Boolean>
    // last result evaluated by the mlRepo
    val observableEvaluation: LiveData<Pair<I, O?>>

    val liveInput: SharedFlow<I>
}


interface ILastClient<I, O, FR> : IClient {
    val lastEvaluationsMap: Map<I, O?>
    val lastFinalResult: ObservableData<FR?>
}


interface IProducerClient<I> : IClient {
    suspend fun ready ()
    suspend fun start ()
    suspend fun produceInput (input: I)
    suspend fun stop (cause: ExternalCancellationException = ExternalCancellationException())
}

interface IMachineLearningClient<I, O : IMachineLearningOutput, FR : IMachineLearningFinalResult>
    : ILastClient<I, O, FR>, ILiveClient<I, O>, IProducerClient<I> {
    // the index of the partialResult
    // TODO: Move to view model
    val partialResultEvent: LiveData<PartialEvaluationStateInterface>

    // array of evaluated items by the mlRepo
    val currentResultsList: List<O?>

    val lastResultsList: List<O?>

    val finalResult: ObservableData<FR?>

    fun listen (scope: CoroutineScope, evaluationFlow: SharedFlow<LiveEvaluationStateInterface>?)
}