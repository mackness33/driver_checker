package com.example.driverchecker.machinelearning.helpers.producers

import com.example.driverchecker.machinelearning.data.LiveEvaluationState
import com.example.driverchecker.utils.MutableObservableData
import com.example.driverchecker.utils.ObservableData
import com.example.driverchecker.utils.StatefulData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.sync.Mutex

abstract class AAtomicProducer<S> (
    replay: Int,
    extraBufferCapacity: Int,
    onBufferOverflow: BufferOverflow = BufferOverflow.DROP_OLDEST,
    scope: CoroutineScope? = null
) : AProducer<S>(replay, extraBufferCapacity, onBufferOverflow, scope) {
    protected  val mutex = Mutex()

    override suspend fun emit(state: S) {
        mutex.lock()
        super.emit(state)
        mutex.unlock()
    }

    override fun tryEmit(state: S) : Boolean {
        var res = mutex.tryLock()

        if (res) {
            res = super.tryEmit(state)
            mutex.unlock()
        }

        return res
    }
}