package com.example.driverchecker.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.example.driverchecker.machinelearning.data.SettingsState
import com.example.driverchecker.machinelearning.data.SettingsStateInterface
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.io.IOException

class PreferencesRepository (
    private val dataStore: DataStore<Preferences>,
    context: Context
) {
    private var modelPreferencesFlag: Boolean = false
    val preferencesCategories: Map<String, Set<String>> = mapOf(
        "model" to setOf(
            MODEL_THRESHOLD_NAME,
            MODEL_UOI_THRESHOLD_NAME
        )
    )
    private val preferencesFlags: MutableMap<String, Boolean> = mutableMapOf(
        "model" to false
    )
    private val preferencesValues: MutableMap<String, IPreferences?> =
        preferencesCategories.keys.associateWith { null }.toMutableMap()


    private object PreferencesKeys {
        val MODEL_THRESHOLD_KEY = intPreferencesKey(MODEL_THRESHOLD_NAME)
        val MODEL_UOI_THRESHOLD_KEY = intPreferencesKey(MODEL_UOI_THRESHOLD_NAME)
    }

    companion object {
        const val MODEL_THRESHOLD_NAME = "model_threshold"
        const val MODEL_UOI_THRESHOLD_NAME = "model_uoi_threshold"
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
//                preferencesValues.values.fold(true) {last, current -> last && current != null} -> buildModelPreferences(
//                    preferences[PreferencesKeys.MODEL_THRESHOLD_KEY] ?: 0,
//                    preferences[PreferencesKeys.MODEL_UOI_THRESHOLD_KEY] ?: 0
//                )
                preferencesValues["model"] != null -> buildModelPreferences(
                    preferences[PreferencesKeys.MODEL_THRESHOLD_KEY] ?: 0,
                    preferences[PreferencesKeys.MODEL_UOI_THRESHOLD_KEY] ?: 0
                )
                else -> SettingsState.NoSettings
            }
        }

    private fun buildModelPreferences(threshold: Int, uoiThreshold: Int) : SettingsState.ModelSettings {
        return SettingsState.ModelSettings(
            threshold.toFloat() / 100,
            uoiThreshold.toFloat() / 100
        )
    }

    val sharedPreferencesListener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { preferences, key ->
            val category: String? = getCategoryFromPreferenceKey(key)
            if ( category != null) {
                preferencesValues[category] = when (category) {
                    "model" -> ModelPreferences(
                        preferences.getInt(MODEL_THRESHOLD_NAME, 0),
                        preferences.getInt(MODEL_UOI_THRESHOLD_NAME, 0),
                    )
                    else -> null
                }
            }
        }

    private fun getCategoryFromPreferenceKey (key: String) : String? {
        return preferencesCategories.entries.find { entry -> entry.value.contains(key) }?.key
    }

    fun commit() = runBlocking {
        when {
//                preferencesValues.values.fold(true) {last, current -> last && current != null} -> buildModelPreferences(
//                    preferences[PreferencesKeys.MODEL_THRESHOLD_KEY] ?: 0,
//                    preferences[PreferencesKeys.MODEL_UOI_THRESHOLD_KEY] ?: 0
//                )
            preferencesValues["model"] != null -> updateModelPreferences(
                preferencesValues["model"] as ModelPreferences
            )
            else -> SettingsState.NoSettings
        }
    }

    private suspend fun updateModelPreferences(modelPreferences: ModelPreferences) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.MODEL_THRESHOLD_KEY] = modelPreferences.threshold
            preferences[PreferencesKeys.MODEL_UOI_THRESHOLD_KEY] = modelPreferences.uoiThreshold
        }
    }
}

sealed interface IPreferences
data class ModelPreferences (val threshold: Int, val uoiThreshold: Int) : IPreferences