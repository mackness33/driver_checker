package com.example.driverchecker.machinelearning.general

import android.net.Uri
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.general.local.LiveEvaluationStateInterface
import com.example.driverchecker.machinelearning.general.local.MLLocalRepository
import com.example.driverchecker.machinelearning.general.remote.MLRemoteRepository
import com.example.driverchecker.machinelearning.imagedetection.ImageDetectionRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

abstract class MachineLearningMergerRepository<Data, Prediction, Superclass, Result : MachineLearningArrayListOutput<Data, Prediction, Superclass>> : IMachineLearningRepository<Data, Result> {
    protected var activeKey: String? = null
    protected var repositories: MutableMap<String, IMachineLearningRepository<Data, Result>> = mutableMapOf()

    constructor (initializers: Map<String, Uri>) {
        repositories.putIfAbsent("Local", MachineLearningRepository())
        repositories.putIfAbsent("Remote", MachineLearningRepository())
    }

    override val analysisProgressState: SharedFlow<LiveEvaluationStateInterface<Result>>?
        get() = if (!activeKey.isNullOrBlank() && repositories.containsKey(activeKey)) repositories[activeKey]?.analysisProgressState else null

    override suspend fun instantClassification(input: Data): Result? {
        if (!activeKey.isNullOrBlank() && repositories.containsKey(activeKey))
            return repositories[activeKey]?.instantClassification(input)

        return null
    }

    override suspend fun continuousClassification(input: List<Data>): Result? {
        return null
    }

    override suspend fun continuousClassification(input: Flow<Data>, scope: CoroutineScope): Result? {
        if (!activeKey.isNullOrBlank() && repositories.containsKey(activeKey))
            return repositories[activeKey]?.continuousClassification(input, scope)

        return null
    }

    override suspend fun onStartLiveClassification(input: SharedFlow<Data>, scope: CoroutineScope) {
        if (!activeKey.isNullOrBlank() && repositories.containsKey(activeKey))
            repositories[activeKey]?.onStartLiveClassification(input, scope)
    }

    override suspend fun onStopLiveClassification() {
        if (!activeKey.isNullOrBlank() && repositories.containsKey(activeKey))
            repositories[activeKey]?.onStopLiveClassification()
    }

    override fun <ModelInit> updateModel(init: ModelInit) {
        if (!activeKey.isNullOrBlank() && repositories.containsKey(activeKey))
            repositories[activeKey]?.updateModel(init)
    }

    fun activate (mode: String) {
        activeKey = if (repositories.containsKey(mode)) mode else activeKey
    }
}