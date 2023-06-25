package com.example.driverchecker.machinelearning_old.imagedetection

import com.example.driverchecker.machinelearning.data.IImageDetectionBox
import com.example.driverchecker.machinelearning_old.data.*
import com.example.driverchecker.machinelearning_old.general.local.LiveEvaluationStateInterface
import com.example.driverchecker.machinelearning_old.general.remote.MLRemoteModel
import com.example.driverchecker.machinelearning_old.general.remote.MLRemoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class ImageDetectionRemoteRepository (override val model: MLRemoteModel<IImageDetectionData, ImageDetectionArrayListOutput<String>>?) : MLRemoteRepository<IImageDetectionData, IImageDetectionBox, String, ImageDetectionArrayListOutput<String>>(model) {
    override suspend fun onStopLiveClassification() {
        TODO("Not yet implemented")
    }

    override val analysisProgressState: StateFlow<LiveEvaluationStateInterface<ImageDetectionArrayListOutput<String>>>?
        get() = TODO("Not yet implemented")
    override val repositoryScope: CoroutineScope
        get() = TODO("Not yet implemented")

    override suspend fun continuousClassification(input: List<IImageDetectionData>): ImageDetectionArrayListOutput<String>? {
        TODO("Not yet implemented")
    }

    override suspend fun continuousClassification(
        input: Flow<IImageDetectionData>,
        scope: CoroutineScope
    ): ImageDetectionArrayListOutput<String>? {
        TODO("Not yet implemented")
    }

    override suspend fun onStartLiveClassification(
        input: SharedFlow<IImageDetectionData>,
        scope: CoroutineScope
    ) {
        TODO("Not yet implemented")
    }
}