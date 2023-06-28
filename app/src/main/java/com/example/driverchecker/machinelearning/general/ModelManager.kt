package com.example.driverchecker.machinelearning.general

import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.pytorch.YOLOModel

class ModelManager {
    fun expose (modelName: String, modelInit: Map<String, Any?>) : Any? {
        try {
            when (modelName){
                "YoloV5" -> {
                        val path: String by modelInit
                        val classifications: String by modelInit
                        return MachineLearningRepository(YOLOModel(path, classifications))
                }
            }
        } catch (e : Throwable) {
            Log.e("ModelManager", e.message ?: "Error on the exposition of the model $modelName")
        }
        return null
    }
}