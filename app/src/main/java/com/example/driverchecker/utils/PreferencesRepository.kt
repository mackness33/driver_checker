package com.example.driverchecker.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.example.driverchecker.machinelearning.data.SettingsState
import com.example.driverchecker.machinelearning.data.SettingsStateInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class PreferencesRepository (
    private val dataStore: DataStore<Preferences>,
    context: Context
) {
    private var modelPreferencesFlag: Boolean = false

    private object PreferencesKeys {
        val MODEL_THRESHOLD = intPreferencesKey("model_threshold")
        val MODEL_UOI_THRESHOLD = intPreferencesKey("model_uoi_threshold")
    }

    val preferencesFlow: Flow<SettingsStateInterface> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            return@map when {
                modelPreferencesFlag -> buildModelPreferences(
                    preferences[PreferencesKeys.MODEL_THRESHOLD] ?: 0,
                    preferences[PreferencesKeys.MODEL_UOI_THRESHOLD] ?: 0
                )
                else -> SettingsState.NoSettings
            }
        }

    private fun buildModelPreferences(threshold: Int, uoiThreshold: Int) : SettingsState.ModelSettings {
        modelPreferencesFlag = false
        return SettingsState.ModelSettings(
            threshold.toFloat() / 100,
            uoiThreshold.toFloat() / 100
        )
    }

    suspend fun updateModelPreferences(
        modelThreshold: Int,
        modelUOIThreshold: Int
    ) {
        modelPreferencesFlag = true
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.MODEL_THRESHOLD] = modelThreshold
            preferences[PreferencesKeys.MODEL_UOI_THRESHOLD] = modelUOIThreshold
        }
    }
}