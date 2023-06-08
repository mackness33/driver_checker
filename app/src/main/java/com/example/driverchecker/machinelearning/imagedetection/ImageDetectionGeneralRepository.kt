package com.example.driverchecker.machinelearning.imagedetection

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import com.example.driverchecker.machinelearning.data.ImageDetectionBox
import com.example.driverchecker.machinelearning.data.ImageDetectionInput
import com.example.driverchecker.machinelearning.data.MLResult
import com.example.driverchecker.machinelearning.data.ResultContainer
import com.example.driverchecker.machinelearning.general.MLRepositoryInterface
import com.example.driverchecker.machinelearning.general.local.MLLocalRepository

interface ImageDetectionGeneralRepository {
    suspend fun instantClassification (path: String, container: ResultContainer? = null) : MLResult<ArrayList<ImageDetectionBox>>? {
        return instantClassification(BitmapFactory.decodeFile(path), container)
    }

    suspend fun instantClassification (bitmap: Bitmap, container: ResultContainer? = null) : MLResult<ArrayList<ImageDetectionBox>>?
}