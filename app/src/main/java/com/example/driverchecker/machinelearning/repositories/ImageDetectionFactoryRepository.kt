package com.example.driverchecker.machinelearning.repositories

import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.listeners.ClientStateListener
import com.example.driverchecker.machinelearning.helpers.listeners.GenericMode
import com.example.driverchecker.machinelearning.helpers.listeners.IGenericListener
import com.example.driverchecker.machinelearning.helpers.windows.helpers.SingleGroupImageDetectionTag
import com.example.driverchecker.machinelearning.helpers.windows.multiples.IClassificationMultipleWindows
import com.example.driverchecker.machinelearning.helpers.windows.multiples.ImageDetectionMultipleWindows
import com.example.driverchecker.machinelearning.models.IClassificationModel
import com.example.driverchecker.machinelearning.models.pytorch.YOLOModel
import com.example.driverchecker.machinelearning.repositories.general.AClassificationFactoryRepository
import kotlinx.coroutines.CoroutineScope

class ImageDetectionFactoryRepository
    : AClassificationFactoryRepository<IImageDetectionInputOld, IImageDetectionFullOutputOld<String>, IImageDetectionFinalResult<String>, String> {
    constructor(repositoryScope: CoroutineScope) : super(repositoryScope)
    constructor(modelName: String, modelInit: Map<String, Any?>, repositoryScope: CoroutineScope) : super(modelName, modelInit, repositoryScope)

    override var model: IClassificationModel<IImageDetectionInputOld, IImageDetectionFullOutputOld<String>, String>? = null
    override var clientListener: ClientStateListener? = ClientListener()
    override var modelListener: IGenericListener<Boolean>? = null

    override val collectionOfWindows: IClassificationMultipleWindows<IImageDetectionFullOutputOld<String>, String> = ImageDetectionMultipleWindows(repositoryScope)

    init {
        val semaphores = setOf("model", "client")
        initialize(semaphores)
        (collectionOfWindows as ImageDetectionMultipleWindows).update(
            MultipleWindowSettings (
                setOf(1, 3, 5),
                setOf(SingleGroupImageDetectionTag),
                setOf(0.10f, 0.50f),
                model?.classifier?.supergroups?.keys ?: emptySet()
            )
        )
    }

    override fun use (modelName: String, modelInit: Map<String, Any?>) : Boolean {
        try {
            onStopLiveEvaluation()
            model = factory(modelName, modelInit)
            when {
                modelListener != null -> modelListener?.listen(repositoryScope, model?.isLoaded, GenericMode.First)
                model == null -> modelListener = ModelListener()
                else -> modelListener = ModelListener(repositoryScope, model!!.isLoaded, GenericMode.First)
            }
        } catch (e : Throwable) {
            Log.e("ImageDetectionFactoryRepository", e.message ?: "Error on the exposition of the model $modelName", e)
        }
        return false
    }

    private fun factory (modelName: String, modelInit: Map<String, Any?>) : IClassificationModel<IImageDetectionInputOld, IImageDetectionFullOutputOld<String>, String>? {
        return when (modelName){
            "YoloV5" -> {
                val path = modelInit["path"] as String?
                val classifications = modelInit["classification"] as String?
                YOLOModel(path, classifications, repositoryScope)
            }
            else -> null
        }
    }

    companion object {
        @Volatile private var INSTANCE: ImageDetectionFactoryRepository? = null

        fun getInstance(modelName: String, modelInit: Map<String, Any?>, repositoryScope: CoroutineScope): ImageDetectionFactoryRepository =
            INSTANCE ?: ImageDetectionFactoryRepository(modelName, modelInit, repositoryScope)

        fun getInstance(repositoryScope: CoroutineScope): ImageDetectionFactoryRepository =
            INSTANCE ?: ImageDetectionFactoryRepository(repositoryScope)
    }

    override fun loadClassifications(json: String?): Boolean {
        TODO("Not yet implemented")
    }
}