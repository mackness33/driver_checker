package com.example.driverchecker.machinelearning.data

import android.graphics.Bitmap
import android.graphics.Rect

data class ImageDetectionInput (
    val image: Bitmap,
    val scale: Pair<Float, Float> = Pair(1.0f, 1.0f),
    val vector: Pair<Float, Float> = Pair(1.0f, 1.0f),
    val start: Pair<Float, Float> = Pair(0.0f, 0.0f)
)