package com.example.driverchecker.machinelearning.general

import android.graphics.Bitmap
import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.pytorch.YOLOModel

interface MachineLearningFactory<Data, Result : WithConfidence>
    : IMachineLearningRepository<Data, Result> {
    fun use (modelName: String, modelInit: Map<String, Any?>) : Boolean
}