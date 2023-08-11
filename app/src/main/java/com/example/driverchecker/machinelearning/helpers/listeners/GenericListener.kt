package com.example.driverchecker.machinelearning.helpers.listeners

import com.example.driverchecker.utils.AtomicValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

interface GenericListener<S> {
    fun listen (scope: CoroutineScope, inputFlow: SharedFlow<S>?) : Job {
        return scope.launch(Dispatchers.Default) {
            inputFlow?.collect { state -> collectClientStates(state)}
        }
    }

    fun destroy () {
        job?.cancel()
    }

    suspend fun collectClientStates (state: S) {
        currentState.update(state)
    }

    val currentState: AtomicValue<S?>

    val job: Job?
}