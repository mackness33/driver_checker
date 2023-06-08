package com.example.driverchecker.machinelearning.imagedetection

import android.graphics.Bitmap
import com.example.driverchecker.machinelearning.data.ResultContainer

interface ImageDetectionRepositoryInterface<Data> {
    suspend fun buildInput (bitmap: Bitmap, container: ResultContainer) : Data? = null
}