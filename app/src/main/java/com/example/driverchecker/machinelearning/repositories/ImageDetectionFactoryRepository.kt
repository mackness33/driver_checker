package com.example.driverchecker.machinelearning.repositories

import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.models.IClassificationModel
import com.example.driverchecker.machinelearning.models.IMachineLearningModel
import com.example.driverchecker.machinelearning.models.pytorch.YOLOModel
import com.example.driverchecker.machinelearning.repositories.general.MachineLearningFactoryRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect

class ImageDetectionFactoryRepository
    : MachineLearningFactoryRepository<IImageDetectionData, ImageDetectionArrayListOutput<String>> {

    constructor() : super()

    constructor(modelName: String, modelInit: Map<String, Any?>) : super(modelName, modelInit)

    override var model: IMachineLearningModel<IImageDetectionData, ImageDetectionArrayListOutput<String>>? = null

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

    override fun jobClassification (input: Flow<IImageDetectionData>, scope: CoroutineScope): Job {
        return repositoryScope.launch(Dispatchers.Default) {
            // check if the repo is ready to make evaluations
            if (_externalProgressState.replayCache.last() == LiveEvaluationState.Ready(true)) {
                _externalProgressState.emit(LiveClassificationState.Start((model as IClassificationModel<IImageDetectionData, ImageDetectionArrayListOutput<String>, String>).classifier.maxClassesInGroup()))

                flowClassification(input, ::cancel)?.collect()
            } else {
                _externalProgressState.emit(LiveEvaluationState.End(Throwable("The stream is not ready yet"), null))
                _externalProgressState.emit(LiveEvaluationState.Ready(model?.isLoaded?.value ?: false))
            }
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