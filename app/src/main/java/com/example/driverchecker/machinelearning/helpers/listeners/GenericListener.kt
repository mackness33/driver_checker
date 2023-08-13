package com.example.driverchecker.machinelearning.helpers.listeners

import com.example.driverchecker.utils.AtomicValue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

open class GenericListener<S> : IGenericListener<S> {
    final override var job: Job? = null
        protected set

    final override val currentState: AtomicValue<S?> = AtomicValue(null)

    constructor () {}

    constructor (scope: CoroutineScope, inputFlow: SharedFlow<S>, mode: IGenericMode = GenericMode.None){
        initJob(scope, inputFlow, mode)
    }

    override fun listen (scope: CoroutineScope, inputFlow: SharedFlow<S>?, mode: IGenericMode) {
        destroy()

        job = scope.launch(Dispatchers.Default) {
            when (mode) {
                GenericMode.First -> currentState.update(inputFlow?.replayCache?.first())
                GenericMode.Last -> currentState.update(inputFlow?.replayCache?.last())
                GenericMode.None -> {}
            }
            inputFlow?.collect { state -> collectStates(state)}
        }
    }

    private fun initJob (scope: CoroutineScope, inputFlow: SharedFlow<S>, mode: IGenericMode) = listen (scope, inputFlow, mode)
}