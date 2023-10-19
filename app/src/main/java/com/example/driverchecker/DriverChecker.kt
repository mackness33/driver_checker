package com.example.driverchecker

import android.app.Application
import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.example.driverchecker.database.DriverCheckerRoomDatabase
import com.example.driverchecker.database.ImageDetectionDatabaseRepository
import com.example.driverchecker.machinelearning.repositories.ImageDetectionFactoryRepository
import com.example.driverchecker.media.FileUtils
import com.example.driverchecker.utils.PreferencesRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class DriverChecker : Application() {
    // No need to cancel this scope as it'll be torn down with the process
    private val applicationScope = CoroutineScope(SupervisorJob())
    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = IMAGE_DETECTION_PREFERENCES_NAME)

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { DriverCheckerRoomDatabase.getDatabase(this, applicationScope) }
//    val testRepository by lazy { TestRepo(database.testDao()) }

    val imageDetectionDatabaseRepository by lazy { ImageDetectionDatabaseRepository(
        database.evaluationDao(),
        database.partialDao(),
        database.itemDao(),
        database.metricsDao(),
        database.windowInformationDao(),
        database.groupMetricsDao()
    )}

    val preferencesRepository by lazy { PreferencesRepository(this.dataStore, applicationScope) }

    val repository by lazy { ImageDetectionFactoryRepository.getInstance(
        "YoloV5",
        mapOf(
            "path" to FileUtils.assetFilePath(this, "new_4_classes.ptl"),
            "classification" to FileUtils.assetLoadJson(this, "classification.json"),
        ),
        applicationScope,
    )}

    companion object {
        private const val IMAGE_DETECTION_PREFERENCES_NAME = "image_detection_settings"
    }
}