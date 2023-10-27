package com.example.driverchecker.machinelearning.helpers.listeners

import com.example.driverchecker.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

abstract class AGenericListener<S> : IGenericListener<S> {
    final override var job: Job? = null
        protected set

    protected val mCurrentState: AtomicObservableData<S?> = LockableData(null)
    final override val currentState: ObservableData<S?> = mCurrentState

    constructor () {}

    constructor (scope: CoroutineScope, inputFlow: SharedFlow<S>, mode: IGenericMode = GenericMode.None){
        initJob(scope, inputFlow, mode)
    }

    override fun listen (scope: CoroutineScope, inputFlow: SharedFlow<S>?, mode: IGenericMode) {
        destroy()

        job = scope.launch(Dispatchers.Default) {
            when (mode) {
                GenericMode.First -> if (!inputFlow?.replayCache.isNullOrEmpty()) mCurrentState.update(inputFlow?.replayCache?.first())
                GenericMode.Last -> if (!inputFlow?.replayCache.isNullOrEmpty()) mCurrentState.update(inputFlow?.replayCache?.last())
                GenericMode.None -> {}
            }
            inputFlow?.collect { state -> collectStates(state)}
        }
    }

    private fun initJob (scope: CoroutineScope, inputFlow: SharedFlow<S>, mode: IGenericMode) = listen (scope, inputFlow, mode)

    override suspend fun collectStates (state: S) {
        mCurrentState.update(state)
    }

    protected suspend fun genericCollectStates (state: S) {
        mCurrentState.update(state)
    }
}