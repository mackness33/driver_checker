package com.example.driverchecker.machinelearning.repositories.general

import android.util.Log
import com.example.driverchecker.machinelearning.data.IMachineLearningData
import com.example.driverchecker.machinelearning.data.LiveEvaluationStateInterface
import com.example.driverchecker.machinelearning.data.MachineLearningArrayListBaseOutput
import com.example.driverchecker.machinelearning.models.pytorch.YOLOModel
import com.example.driverchecker.machinelearning.repositories.IMachineLearningRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

abstract class MachineLearningMergerRepository
    :
    IMachineLearningRepository<IMachineLearningData<Any?>, MachineLearningArrayListBaseOutput<Any?>> {
    protected var activeKey: String? = null
    protected var repositories: MutableMap<String, IMachineLearningRepository<IMachineLearningData<Any?>, MachineLearningArrayListBaseOutput<Any?>>> = mutableMapOf()
    override val repositoryScope: CoroutineScope = CoroutineScope(SupervisorJob())

//    constructor(initializers: Map<String, >?) {
//
//        repositories.putIfAbsent("Local", MachineLearningRepository())
//        repositories.putIfAbsent("Remote", MachineLearningRepository())
//    }


    override val analysisProgressState: SharedFlow<LiveEvaluationStateInterface<MachineLearningArrayListBaseOutput<Any?>>>?
        get() = if (!activeKey.isNullOrBlank() && repositories.containsKey(activeKey)) repositories[activeKey]?.analysisProgressState else null

    override suspend fun instantClassification(input: IMachineLearningData<Any?>): MachineLearningArrayListBaseOutput<Any?>? {
        if (!activeKey.isNullOrBlank() && repositories.containsKey(activeKey))
            return repositories[activeKey]?.instantClassification(input)

        return null
    }

    override suspend fun continuousClassification(input: List<IMachineLearningData<Any?>>): MachineLearningArrayListBaseOutput<Any?>? {
        return null
    }

    override suspend fun continuousClassification(input: Flow<IMachineLearningData<Any?>>, scope: CoroutineScope): MachineLearningArrayListBaseOutput<Any?>? {
        if (!activeKey.isNullOrBlank() && repositories.containsKey(activeKey))
            return repositories[activeKey]?.continuousClassification(input, scope)

        return null
    }

    override fun onStartLiveClassification(input: SharedFlow<IMachineLearningData<Any?>>, scope: CoroutineScope) {
        if (!activeKey.isNullOrBlank() && repositories.containsKey(activeKey))
            repositories[activeKey]?.onStartLiveClassification(input, scope)
    }

    override fun onStopLiveClassification() {
        if (!activeKey.isNullOrBlank() && repositories.containsKey(activeKey))
            repositories[activeKey]?.onStopLiveClassification()
    }

    override fun <ModelInit> updateModel(init: ModelInit) {
        if (!activeKey.isNullOrBlank() && repositories.containsKey(activeKey))
            repositories[activeKey]?.updateModel(init)
    }

    fun activate (model: String) {
        activeKey = if (repositories.containsKey(model)) model else activeKey
    }

    fun use (modelName: String, modelInit: Map<String, Any?>) : Any? {
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

//    companion object {
//        @Volatile private var INSTANCE: MachineLearningMergerRepository? = null
//
////        fun getInstance(localUri: String?, remoteUri: String?): ImageDetectionRepository =
////            INSTANCE ?: ImageDetectionRepository(localUri, remoteUri)
//
//        fun getInstance(config: String): MachineLearningMergerRepository =
//            INSTANCE ?: MachineLearningMergerRepository(config)
//
//    }
}