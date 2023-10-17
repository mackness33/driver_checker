package com.example.driverchecker.machinelearning.helpers.listeners

import com.example.driverchecker.machinelearning.data.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.SharedFlow

abstract class ASettingsStateListener : SettingsStateListener, AGenericListener<SettingsStateInterface> {
    constructor () : super()

    constructor (scope: CoroutineScope, inputFlow: SharedFlow<SettingsStateInterface>, mode: IGenericMode = GenericMode.None) :
            super(scope, inputFlow, mode)

    override suspend fun collectStates (state: SettingsStateInterface) {
        super.collectStates(state)
        when (state) {
            is SettingsState.FullSettings -> TODO()
            is SettingsState.ModelSettings -> onModelSettingsChange(state)
            is SettingsState.WindowSettings -> TODO()
            SettingsState.NoSettings -> onNoSettingsChange()
        }
    }
}