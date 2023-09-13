package com.example.driverchecker

import android.app.Application
import com.example.driverchecker.database.DriverCheckerRoomDatabase
import com.example.driverchecker.database.EvaluationRepository
import com.example.driverchecker.machinelearning.repositories.ImageDetectionFactoryRepository
import com.example.driverchecker.media.FileUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class DriverChecker : Application() {
    // No need to cancel this scope as it'll be torn down with the process
    // TODO: pass the application scope to the repository of the ImageDetection, to assure that the calculation and etc, will not teardown unless the application is done.
    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
//    val defaultModel: Map<String, String> =

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val database by lazy { DriverCheckerRoomDatabase.getDatabase(this, applicationScope) }
//    val testRepository by lazy { TestRepo(database.testDao()) }
    val evaluationRepository by lazy { EvaluationRepository(database.evaluationDao(), database.partialDao(), database.itemDao(), database.metricsDao())}

    val repository by lazy { ImageDetectionFactoryRepository.getInstance(
        "YoloV5",
        mapOf(
            "path" to FileUtils.assetFilePath(this, "weak_four_classes.ptl"),
            "classification" to FileUtils.assetLoadJson(this, "classification.json"),
        ),
        applicationScope,
    )}
}