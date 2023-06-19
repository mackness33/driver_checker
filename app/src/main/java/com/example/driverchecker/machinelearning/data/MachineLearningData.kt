package com.example.driverchecker.machinelearning.data

import android.graphics.Bitmap

data class MachineLearningData<R>(val result: R, val confidence: Float, val classes: List<Int>, val bitmap: Bitmap)