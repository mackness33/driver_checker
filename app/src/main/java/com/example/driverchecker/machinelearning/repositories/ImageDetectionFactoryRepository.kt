package com.example.driverchecker.machinelearning.repositories

import android.graphics.ColorSpace.Model
import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.listeners.ClientStateListener
import com.example.driverchecker.machinelearning.helpers.listeners.IGenericListener
import com.example.driverchecker.machinelearning.models.IClassificationModel
import com.example.driverchecker.machinelearning.models.pytorch.YOLOModel
import com.example.driverchecker.machinelearning.repositories.general.AClassificationFactoryRepository

class ImageDetectionFactoryRepository
    : AClassificationFactoryRepository<IImageDetectionInput, IImageDetectionOutput<String>, IImageDetectionFinalResult<String>, String> {

    constructor() : super()

    constructor(modelName: String, modelInit: Map<String, Any?>) : super(modelName, modelInit)

    override var model: IClassificationModel<IImageDetectionInput, IImageDetectionOutput<String>, String>? = null
    override var clientListener: ClientStateListener? = ClientListener()
    override var modelListener: IGenericListener<Boolean>? = ModelListener()

    override fun use (modelName: String, modelInit: Map<String, Any?>) : Boolean {
        try {
            onStopLiveEvaluation()
            model = factory(modelName, modelInit)
            modelListener?.listen(repositoryScope, model?.isLoaded)
            listenModelState()
            return model?.isLoaded?.value ?: false
        } catch (e : Throwable) {
            Log.e("ImageDetectionFactoryRepository", e.message ?: "Error on the exposition of the model $modelName")
        }
        return false
    }

    protected fun factory (modelName: String, modelInit: Map<String, Any?>) : IClassificationModel<IImageDetectionInput, IImageDetectionOutput<String>, String>? {
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

    override fun loadClassifications(json: String?): Boolean {
        TODO("Not yet implemented")
    }
}