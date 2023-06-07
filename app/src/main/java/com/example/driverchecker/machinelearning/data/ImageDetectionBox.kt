package com.example.driverchecker.machinelearning.data

import android.graphics.Rect

data class ImageDetectionBox (var classIndex: Int, var score: Float, var rect: Rect)