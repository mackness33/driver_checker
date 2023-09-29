package com.example.driverchecker.machinelearning.repositories

import android.util.Log
import com.example.driverchecker.machinelearning.collections.ClassificationWindowsMutableCollection
import com.example.driverchecker.machinelearning.collections.ImageDetectionSetOfWindows
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.listeners.ClientStateListener
import com.example.driverchecker.machinelearning.helpers.listeners.GenericMode
import com.example.driverchecker.machinelearning.helpers.listeners.IGenericListener
import com.example.driverchecker.machinelearning.models.IClassificationModel
import com.example.driverchecker.machinelearning.models.pytorch.YOLOModel
import com.example.driverchecker.machinelearning.repositories.general.AClassificationFactoryRepository
import kotlinx.coroutines.CoroutineScope

class ImageDetectionFactoryRepository
    : AClassificationFactoryRepository<IImageDetectionInput, IImageDetectionFullOutput<String>, IImageDetectionFinalResult<String>, String> {

    constructor(repositoryScope: CoroutineScope) : super(repositoryScope)

    constructor(modelName: String, modelInit: Map<String, Any?>, repositoryScope: CoroutineScope) : super(modelName, modelInit, repositoryScope)

    override var model: IClassificationModel<IImageDetectionInput, IImageDetectionFullOutput<String>, String>? = null
    override var clientListener: ClientStateListener? = ClientListener()
    override var modelListener: IGenericListener<Boolean>? = null

    override val collectionOfWindows: ClassificationWindowsMutableCollection<IImageDetectionFullOutput<String>, String> = ImageDetectionSetOfWindows(repositoryScope)

    init {
        initialize()
    }

    override fun use (modelName: String, modelInit: Map<String, Any?>) : Boolean {
        try {
            onStopLiveEvaluation()
            model = factory(modelName, modelInit)
            if (modelListener == null)
                modelListener =
                    if (model == null) ModelListener()
                    else ModelListener(repositoryScope, model!!.isLoaded, GenericMode.First)
            else
                modelListener?.listen(repositoryScope, model?.isLoaded, GenericMode.First)
            /*
            when {
                modelListener != null -> modelListener?.listen(repositoryScope, model?.isLoaded, GenericMode.First)
                model == null -> modelListener = ModelListener()
                else -> modelListener = ModelListener(repositoryScope, model!!.isLoaded, GenericMode.First)
            }
            * */
            return false
        } catch (e : Throwable) {
            Log.e("ImageDetectionFactoryRepository", e.message ?: "Error on the exposition of the model $modelName")
        }
        return false
    }

    protected fun factory (modelName: String, modelInit: Map<String, Any?>) : IClassificationModel<IImageDetectionInput, IImageDetectionFullOutput<String>, String>? {
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

        fun getInstance(modelName: String, modelInit: Map<String, Any?>, repositoryScope: CoroutineScope): ImageDetectionFactoryRepository =
            INSTANCE ?: ImageDetectionFactoryRepository(modelName, modelInit, repositoryScope)

        fun getInstance(repositoryScope: CoroutineScope): ImageDetectionFactoryRepository =
            INSTANCE ?: ImageDetectionFactoryRepository(repositoryScope)
    }

    override fun loadClassifications(json: String?): Boolean {
        TODO("Not yet implemented")
    }
}