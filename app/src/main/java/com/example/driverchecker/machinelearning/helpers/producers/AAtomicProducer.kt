package com.example.driverchecker.machinelearning.helpers.producers

import com.example.driverchecker.machinelearning.data.LiveEvaluationState
import com.example.driverchecker.utils.MutableObservableData
import com.example.driverchecker.utils.ObservableData
import com.example.driverchecker.utils.StatefulData
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.*

abstract class AAtomicProducer<S> (
    replay: Int,
    extraBufferCapacity: Int,
    onBufferOverflow: BufferOverflow = BufferOverflow.DROP_OLDEST,
    val scope: CoroutineScope? = null
) : IProducer<S> {
    protected val mSharedFlow: MutableSharedFlow<S>
    override val sharedFlow: SharedFlow<S>
        get() = mSharedFlow.asSharedFlow()

    init {
        mSharedFlow = MutableSharedFlow(replay, extraBufferCapacity, onBufferOverflow)
    }

    protected val mCurrentState: MutableObservableData<S?> = StatefulData(null)
    override val currentState: ObservableData<S?>
        get() = mCurrentState

    protected open suspend fun emit(state: S) {
        mSharedFlow.emit(state)
        mCurrentState.postValue(state)
    }

    protected open fun tryEmit(state: S) : Boolean {
        val res = mSharedFlow.tryEmit(state)
        if (res)
            mCurrentState.postValue(state)

        return res
    }

    override fun isLast(state: S) : Boolean {
        return if (mSharedFlow.replayCache.isEmpty()) false else mSharedFlow.replayCache.last() == state
    }
}