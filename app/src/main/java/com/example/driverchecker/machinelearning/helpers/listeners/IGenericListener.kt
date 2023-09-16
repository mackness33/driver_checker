package com.example.driverchecker.machinelearning.helpers.listeners

import com.example.driverchecker.utils.ObservableData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharedFlow

interface IGenericListener<S> {
    fun listen (scope: CoroutineScope, inputFlow: SharedFlow<S>?, mode: IGenericMode = GenericMode.None)

    fun destroy () {
        job?.cancel()
    }

    suspend fun collectStates (state: S)

    val currentState: ObservableData<S?>

    val job: Job?
}


sealed interface IGenericMode

sealed class GenericMode : IGenericMode {
    object None : GenericMode()
    object First : GenericMode()
    object Last : GenericMode()
}