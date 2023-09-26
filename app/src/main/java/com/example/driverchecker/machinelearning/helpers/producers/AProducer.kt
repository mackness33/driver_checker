package com.example.driverchecker.machinelearning.helpers.producers

import com.example.driverchecker.machinelearning.data.LiveEvaluationState
import com.example.driverchecker.utils.MutableObservableData
import com.example.driverchecker.utils.ObservableData
import com.example.driverchecker.utils.StatefulData
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

abstract class AProducer<S> (
    replay: Int,
    extraBufferCapacity: Int,
    onBufferOverflow: BufferOverflow = BufferOverflow.DROP_OLDEST
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

    protected suspend fun emit(state: S) {
        mSharedFlow.emit(state)
        mCurrentState.postValue(state)
    }

    protected fun tryEmit(state: S) : Boolean {
        val res = mSharedFlow.tryEmit(state)
        if (res)
            mCurrentState.postValue(state)

        return res
    }

    override fun isLast(state: S) : Boolean {
        return mSharedFlow.replayCache.last() == state
    }
}