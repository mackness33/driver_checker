package com.example.driverchecker.machinelearning.data

import android.graphics.Bitmap
import android.graphics.RectF

// ---------------------------------- CLASSES ----------------------------------

interface IClassification {
    val name: String
    val index: Int
}

data class Classification (override val name: String, override val index: Int) : IClassification