package com.example.driverchecker.machinelearning.helpers.listeners

import com.example.driverchecker.machinelearning.data.LiveEvaluationStateInterface
import com.example.driverchecker.utils.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.launch

open class GenericListener<S> : AGenericListener<S> {
    constructor () : super()
    constructor (scope: CoroutineScope, inputFlow: SharedFlow<S>, mode: IGenericMode = GenericMode.None) :
            super(scope, inputFlow, mode)
}
