package com.example.driverchecker.machinelearning.data

import android.graphics.Rect
import android.graphics.RectF

data class ImageDetectionBox (var classIndex: Int, var score: Float, var rect: RectF)