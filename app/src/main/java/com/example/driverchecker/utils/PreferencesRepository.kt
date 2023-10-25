package com.example.driverchecker.utils

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.*
import com.example.driverchecker.MainActivity
import com.example.driverchecker.machinelearning.data.SettingsState
import com.example.driverchecker.machinelearning.data.SettingsStateInterface
import com.example.driverchecker.machinelearning.windows.helpers.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.runBlocking
import java.io.IOException

class PreferencesRepository (
    private val dataStore: DataStore<Preferences>,
    private val scope: CoroutineScope,
//    context: Context
) {

//    private val sharedPreferences =
//        context.applicationContext.getSharedPreferences(MainActivity.IMAGE_DETECTION_PREFERENCES_NAME, Context.MODE_PRIVATE)

    private val tagsMap: Map<String, IWindowTag> = mapOf(
        "1" to SingleGroupTag,
        "2" to MultipleGroupTag,
        "3" to SingleGroupOffsetTag,
        "4" to MultipleGroupOffsetTag
    )

    private val preferencesCategories: Map<String, Set<String>> = mapOf(
        "model" to setOf(
            MODEL_THRESHOLD_NAME,
            MODEL_UOI_THRESHOLD_NAME
        ),
        "window" to setOf(
            WINDOW_TYPES_NAME,
            WINDOW_OFFSETS_NAME,
            WINDOW_SIZES_NAME,
            WINDOW_THRESHOLDS_NAME
        )
    )
    private val preferencesValues: MutableMap<String, IPreferences?> =
        preferencesCategories.keys.associateWith { null }.toMutableMap()
    private val mActivePreferences: MutableMap<String, SettingsStateInterface?> =
        preferencesCategories.keys.associateWith { null }.toMutableMap()
    val activePreferences: Map<String, SettingsStateInterface?> = mActivePreferences



    private object PreferencesKeys {
        val MODEL_THRESHOLD_KEY = intPreferencesKey(MODEL_THRESHOLD_NAME)
        val MODEL_UOI_THRESHOLD_KEY = intPreferencesKey(MODEL_UOI_THRESHOLD_NAME)

        val WINDOW_THRESHOLDS_KEY = stringSetPreferencesKey(WINDOW_THRESHOLDS_NAME)
        val WINDOW_SIZES_KEY = stringSetPreferencesKey(WINDOW_SIZES_NAME)
        val WINDOW_OFFSETS_KEY = stringSetPreferencesKey(WINDOW_OFFSETS_NAME)
        val WINDOW_TYPES_KEY = stringSetPreferencesKey(WINDOW_TYPES_NAME)
    }

    val preferencesFlow: SharedFlow<SettingsStateInterface> = dataStore.data
        .catch { exception ->
            // dataStore.data throws an IOException when an error is encountered when reading data
            if (exception is IOException) {
                emit(emptyPreferences())
            } else {
                throw exception
            }
        }
        .map { preferences ->
            val result = when {
                preferencesValues.values.fold(true) {last, current -> last && current != null} -> {
                    mActivePreferences["model"] = buildModelPreferences(
                        preferences[PreferencesKeys.MODEL_THRESHOLD_KEY] ?: 0,
                        preferences[PreferencesKeys.MODEL_UOI_THRESHOLD_KEY] ?: 0
                    )

                    buildFullPreferences(
                        buildModelPreferences(
                            preferences[PreferencesKeys.MODEL_THRESHOLD_KEY] ?: 0,
                            preferences[PreferencesKeys.MODEL_UOI_THRESHOLD_KEY] ?: 0
                        ),
                        buildWindowPreferences(
                            preferences[PreferencesKeys.WINDOW_TYPES_KEY] ?: setOf(),
                            preferences[PreferencesKeys.WINDOW_THRESHOLDS_KEY] ?: setOf(),
                            preferences[PreferencesKeys.WINDOW_SIZES_KEY] ?: setOf(),
                            preferences[PreferencesKeys.WINDOW_OFFSETS_KEY] ?: setOf(),
                        )
                    )
                }
                preferencesValues["model"] != null -> {
                    mActivePreferences["model"] = buildModelPreferences(
                        preferences[PreferencesKeys.MODEL_THRESHOLD_KEY] ?: 0,
                        preferences[PreferencesKeys.MODEL_UOI_THRESHOLD_KEY] ?: 0
                    )

                    buildModelPreferences(
                        preferences[PreferencesKeys.MODEL_THRESHOLD_KEY] ?: 0,
                        preferences[PreferencesKeys.MODEL_UOI_THRESHOLD_KEY] ?: 0
                    )
                }
                preferencesValues["window"] != null -> buildWindowPreferences(
                    preferences[PreferencesKeys.WINDOW_TYPES_KEY] ?: setOf(),
                    preferences[PreferencesKeys.WINDOW_THRESHOLDS_KEY] ?: setOf(),
                    preferences[PreferencesKeys.WINDOW_SIZES_KEY] ?: setOf(),
                    preferences[PreferencesKeys.WINDOW_OFFSETS_KEY] ?: setOf(),
                )
                else -> SettingsState.NoSettings
            }

//            print ("result: $result")
            Log.d("PREFERENCES", "preferences: $preferences")
            Log.d("PREFERENCES", "preferences types: ${preferences[PreferencesKeys.WINDOW_TYPES_KEY]}")
            // Clean the preferencesValues
            preferencesValues.replaceAll { _, _ -> null }

            return@map result
        }.shareIn(scope, SharingStarted.Eagerly, 1)

    private suspend fun initializePreferencesFlow() {
        preferencesValues["model"] = ModelPreferences(0, 0,)
        preferencesValues["window"] = WindowPreferences(setOf(), setOf(), setOf(), setOf())
        dataStore.updateData { prefs -> prefs }
    }

    init {
        runBlocking { initializePreferencesFlow() }
    }

    private fun buildModelPreferences(threshold: Int, uoiThreshold: Int) : SettingsState.ModelSettings {
        return SettingsState.ModelSettings(
            threshold.toFloat() / 100,
            uoiThreshold.toFloat() / 100
        )
    }

    private fun buildFullPreferences(
        modelSettings: SettingsState.ModelSettings,
        windowSettings: SettingsState.WindowSettings
    ) : SettingsState.FullSettings {
        return SettingsState.FullSettings(
            modelSettings,
            windowSettings
        )
    }

    private fun buildWindowPreferences(
        types: Set<String?>,
        thresholds: Set<String?>,
        sizes: Set<String?>,
        offsets: Set<String?>
    ) : SettingsState.WindowSettings {
        Log.d("WINDOWPREFERENCES", types.toString())
        return SettingsState.WindowSettings(
            types.mapNotNull { type -> tagsMap[type] }.toSet(),
            thresholds.mapNotNull { threshold -> threshold?.toFloat()?.div(100) }.toSet(),
            sizes.mapNotNull { frame -> frame?.toInt() }.toSet(),
            offsets.mapNotNull { offset -> offset?.toInt() }.toSet()
        )
    }

    val sharedPreferencesListener: SharedPreferences.OnSharedPreferenceChangeListener =
        SharedPreferences.OnSharedPreferenceChangeListener { preferences, key ->
            val category: String? = getCategoryFromPreferenceKey(key)
            if (category != null) {
                preferencesValues[category] = when (category) {
                    "model" -> ModelPreferences(
                        preferences.getInt(MODEL_THRESHOLD_NAME, 0),
                        preferences.getInt(MODEL_UOI_THRESHOLD_NAME, 0),
                    )
                    "window" -> WindowPreferences(
                        preferences.getStringSet(WINDOW_TYPES_NAME, null) ?: setOf(),
                        preferences.getStringSet(WINDOW_THRESHOLDS_NAME, null) ?: setOf(),
                        preferences.getStringSet(WINDOW_SIZES_NAME, null) ?: setOf(),
                        preferences.getStringSet(WINDOW_OFFSETS_NAME, null) ?: setOf(),
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
            preferencesValues.values.fold(true) {last, current -> last && current != null} -> {
                updateModelPreferences(
                    preferencesValues["model"] as ModelPreferences
                )
                updateWindowPreferences(
                    preferencesValues["window"] as WindowPreferences
                )
            }
            preferencesValues["model"] != null -> updateModelPreferences(
                preferencesValues["model"] as ModelPreferences
            )
            preferencesValues["window"] != null -> updateWindowPreferences(
                preferencesValues["window"] as WindowPreferences
            )
            else -> {}
        }
    }

    private suspend fun updateModelPreferences(modelPreferences: ModelPreferences) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.MODEL_THRESHOLD_KEY] = modelPreferences.threshold
            preferences[PreferencesKeys.MODEL_UOI_THRESHOLD_KEY] = modelPreferences.uoiThreshold
        }
    }

    private suspend fun updateWindowPreferences(windowPreferences: WindowPreferences) {
        dataStore.edit { preferences ->
            preferences[PreferencesKeys.WINDOW_TYPES_KEY] = windowPreferences.types
            preferences[PreferencesKeys.WINDOW_THRESHOLDS_KEY] = windowPreferences.thresholds
            preferences[PreferencesKeys.WINDOW_SIZES_KEY] = windowPreferences.sizes
            preferences[PreferencesKeys.WINDOW_OFFSETS_KEY] = windowPreferences.offsets
        }
    }

    companion object {
//        @Volatile
//        private var INSTANCE: PreferencesRepository? = null
//
//        fun getInstance(dataStore: DataStore<Preferences>, scope: CoroutineScope, context: Context): PreferencesRepository {
//            return INSTANCE ?: synchronized(this) {
//                INSTANCE?.let {
//                    return it
//                }
//
//                val instance = PreferencesRepository(dataStore, scope)
//                INSTANCE = instance
//                instance
//            }
//        }


        const val MODEL_THRESHOLD_NAME = "model_threshold"
        const val MODEL_UOI_THRESHOLD_NAME = "model_uoi_threshold"

        const val WINDOW_THRESHOLDS_NAME = "windows_thresholds"
        const val WINDOW_SIZES_NAME = "windows_sizes"
        const val WINDOW_OFFSETS_NAME = "windows_offsets"
        const val WINDOW_TYPES_NAME = "windows_types"
    }
}

sealed interface IPreferences
data class ModelPreferences (val threshold: Int, val uoiThreshold: Int) : IPreferences
data class WindowPreferences (
    val types: Set<String>,
    val thresholds: Set<String>,
    val sizes: Set<String>,
    val offsets: Set<String>
) : IPreferences