package com.example.driverchecker.machinelearning.helpers.listeners

import com.example.driverchecker.machinelearning.data.*

interface SettingsStateListener : IGenericListener<SettingsStateInterface> {
    // handler of mlRepo in ready
    suspend fun onModelSettingsChange (state: SettingsState.ModelSettings)

    suspend fun onWindowSettingsChange(state: SettingsState.WindowSettings)

    suspend fun onFullSettingsChange(state: SettingsState.FullSettings)

    suspend fun onNoSettingsChange ()
}