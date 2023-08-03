package com.example.driverchecker.machinelearning.repositories

import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.models.IClassificationModel
import com.example.driverchecker.machinelearning.models.IMachineLearningModel
import com.example.driverchecker.machinelearning.models.pytorch.YOLOModel
import com.example.driverchecker.machinelearning.repositories.general.ClassificationFactoryRepository
import com.example.driverchecker.machinelearning.repositories.general.MachineLearningFactoryRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

class ImageDetectionFactoryRepository
    : ClassificationFactoryRepository<IImageDetectionData, IImageDetectionResult<String>, String> {

    constructor() : super()

    constructor(modelName: String, modelInit: Map<String, Any?>) : super(modelName, modelInit)

    override var model: IMachineLearningModel<IImageDetectionData, IImageDetectionResult<String>>? = null

    override fun use (modelName: String, modelInit: Map<String, Any?>) : Boolean {
        try {
            onStopLiveClassification()
            model = factory(modelName, modelInit)
            listenOnLoadingState()
            return model?.isLoaded?.value ?: false
        } catch (e : Throwable) {
            Log.e("ImageDetectionFactoryRepository", e.message ?: "Error on the exposition of the model $modelName")
        }
        return false
    }

    protected fun factory (modelName: String, modelInit: Map<String, Any?>) : IMachineLearningModel<IImageDetectionData, IImageDetectionResult<String>>? {
        return when (modelName){
            "YoloV5" -> {
                val path = modelInit["path"] as String?
                val classifications = modelInit["classification"] as String?
                YOLOModel(path, classifications)
            }
            else -> null
        }
    }

    companion object {
        @Volatile private var INSTANCE: ImageDetectionFactoryRepository? = null

        fun getInstance(modelName: String, modelInit: Map<String, Any?>): ImageDetectionFactoryRepository =
            INSTANCE ?: ImageDetectionFactoryRepository(modelName, modelInit)

        fun getInstance(): ImageDetectionFactoryRepository =
            INSTANCE ?: ImageDetectionFactoryRepository()
    }
}