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

    constructor (scope: CoroutineScope, inputFlow: SharedFlow<S>){
        initJob(scope, inputFlow)
    }

    override fun listen (scope: CoroutineScope, inputFlow: SharedFlow<S>?) {
        destroy()

        job = scope.launch(Dispatchers.Default) {
            inputFlow?.collect { state -> collectClientStates(state)}
        }
    }

    private fun initJob (scope: CoroutineScope, inputFlow: SharedFlow<S>) = listen (scope, inputFlow)
}