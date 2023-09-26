package com.example.driverchecker.machinelearning.helpers.producers

import com.example.driverchecker.utils.ObservableData
import com.example.driverchecker.utils.StatefulData
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

interface IProducer<S> {
    val sharedFlow: SharedFlow<S>

    val currentState: ObservableData<S?>

    fun isLast(state: S) : Boolean
}