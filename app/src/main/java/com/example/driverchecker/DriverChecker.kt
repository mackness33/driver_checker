package com.example.driverchecker

import android.app.Application
import com.example.driverchecker.machinelearning.imagedetection.*

class DriverChecker : Application() {
    // No need to cancel this scope as it'll be torn down with the process
//    val applicationScope = CoroutineScope(SupervisorJob())

    // Using by lazy so the database and the repository are only created when they're needed
    // rather than when the application starts
//    val localRepository by lazy { ImageDetectionLocalRepository(ImageDetectionLocalModel(FileUtils.assetFilePath(this, "coco_detection_lite.ptl"))) }
//    val remoteRepository by lazy { ImageDetectionRemoteRepository(ImageDetectionRemoteModel("somePath")) }
    val repository by lazy { ImageDetectionRepository.getInstance(FileUtils.assetFilePath(this, "best.torchscript.ptl"), "https://detect.roboflow.com/checker-ei67f/1?api_key=R6X2vkBZa49KTGoYyv9y") }
}