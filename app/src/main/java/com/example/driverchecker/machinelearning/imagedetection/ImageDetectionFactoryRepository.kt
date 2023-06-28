package com.example.driverchecker.machinelearning.imagedetection

import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.general.MachineLearningFactoryRepository
import com.example.driverchecker.machinelearning.pytorch.YOLOModel

class ImageDetectionFactoryRepository
    : MachineLearningFactoryRepository<IImageDetectionData, ImageDetectionArrayListOutput<String>> {

    constructor() : super()

    constructor(modelName: String, modelInit: Map<String, Any?>) : super(modelName, modelInit){
        initUseRepo(modelName, modelInit)
    }

    override fun use (modelName: String, modelInit: Map<String, Any?>) : Boolean {
        try {
            return when (modelName){
                "YoloV5" -> {
                    val path: String by modelInit
                    val classifications: String by modelInit
                    model = YOLOModel(path, classifications)
                    onStopLiveClassification()
                    true
                }
                else -> false
            }
        } catch (e : Throwable) {
            Log.e("ModelManager", e.message ?: "Error on the exposition of the model $modelName")
        }
        return false
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