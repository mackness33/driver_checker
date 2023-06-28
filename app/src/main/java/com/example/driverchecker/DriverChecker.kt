package com.example.driverchecker

import android.app.Application
import com.example.driverchecker.machinelearning.imagedetection.ImageDetectionFactoryRepository
import com.example.driverchecker.media.FileUtils

class DriverChecker : Application() {
    // No need to cancel this scope as it'll be torn down with the process
    // TODO: pass the application scope to the repository of the ImageDetection, to assure that the calculation and etc, will not teardown unless the application is done.
//    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
    val defaultModel: Map<String, String> = mapOf(
        "path" to FileUtils.assetFilePath(this, "two_classes.ptl"),
        "classification" to FileUtils.assetLoadJson(this, "classification_example.json")
    )
    val repository by lazy { ImageDetectionFactoryRepository.getInstance("YoloV5", defaultModel) }
}