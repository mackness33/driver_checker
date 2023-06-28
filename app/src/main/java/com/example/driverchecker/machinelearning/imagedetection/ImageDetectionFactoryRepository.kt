package com.example.driverchecker.machinelearning.imagedetection

import android.graphics.ColorSpace.Model
import android.util.Log
import com.example.driverchecker.machinelearning.classification.IClassifierModel
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.general.IMachineLearningModel
import com.example.driverchecker.machinelearning.general.MachineLearningFactoryRepository
import com.example.driverchecker.machinelearning.pytorch.YOLOModel

class ImageDetectionFactoryRepository
    : MachineLearningFactoryRepository<IImageDetectionData, ImageDetectionArrayListOutput<String>> {

    constructor() : super()

    constructor(modelName: String, modelInit: Map<String, Any?>) : super(modelName, modelInit)

    override fun use (modelName: String, modelInit: Map<String, Any?>) : Boolean {
        try {
            onStopLiveClassification()
            model = factory(modelName, modelInit)
            listenOnLoadingState()
            return model?.isLoaded?.value ?: false
        } catch (e : Throwable) {
            Log.e("ModelManager", e.message ?: "Error on the exposition of the model $modelName")
        }
        return false
    }

    protected fun factory (modelName: String, modelInit: Map<String, Any?>) : IMachineLearningModel<IImageDetectionData, ImageDetectionArrayListOutput<String>>? {
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

//        fun getInstance(localUri: String?, remoteUri: String?): ImageDetectionRepository =
//            INSTANCE ?: ImageDetectionRepository(localUri, remoteUri)

        fun getInstance(modelName: String, modelInit: Map<String, Any?>): ImageDetectionFactoryRepository =
            INSTANCE ?: ImageDetectionFactoryRepository(modelName, modelInit)

        fun getInstance(): ImageDetectionFactoryRepository =
            INSTANCE ?: ImageDetectionFactoryRepository()
    }
}