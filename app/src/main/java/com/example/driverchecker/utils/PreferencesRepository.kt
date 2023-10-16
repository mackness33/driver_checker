package com.example.driverchecker.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import java.io.IOException

class PreferencesRepository (
    private val dataStore: DataStore<Preferences>,
    context: Context
) {
    private object PreferencesKeys {
        val MODEL_THRESHOLD = intPreferencesKey("model_threshold")
        val MODEL_UOI_THRESHOLD = intPreferencesKey("model_uoi_threshold")
    }

    val preferencesFlow = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            // Get our show completed value, defaulting to false if not set:
            val modelThreshold = preferences[PreferencesKeys.MODEL_THRESHOLD] ?: 0
            val modelUOIThreshold = preferences[PreferencesKeys.MODEL_UOI_THRESHOLD] ?: 0

            return@map ModelPreferences(
                modelThreshold.toFloat()/100,
                modelUOIThreshold.toFloat()/100
            )
        }

    suspend fun updateModelPreferences(
        modelThreshold: Int,
        modelUOIThreshold: Int
    ) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.MODEL_THRESHOLD] = modelThreshold
            preferences[PreferencesKeys.MODEL_UOI_THRESHOLD] = modelUOIThreshold
        }
    }
}

data class ModelPreferences (
    val modelThreshold: Float,
    val modelUOIThreshold: Float
)