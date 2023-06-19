package com.example.driverchecker.machinelearning.imagedetection

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.general.local.LiveEvaluationStateInterface
import com.example.driverchecker.machinelearning.general.remote.MLRemoteModel
import com.example.driverchecker.machinelearning.general.remote.MLRemoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class ImageDetectionRemoteRepository (override val model: MLRemoteModel<IImageDetectionData, ImageDetectionArrayListOutput>?) : MLRemoteRepository<IImageDetectionData, IImageDetectionBox, ImageDetectionArrayListOutput>(model) {
    override suspend fun onStopLiveClassification() {
        TODO("Not yet implemented")
    }

    override val analysisProgressState: StateFlow<LiveEvaluationStateInterface<ImageDetectionArrayListOutput>>?
        get() = TODO("Not yet implemented")
    override val repositoryScope: CoroutineScope
        get() = TODO("Not yet implemented")

    override suspend fun continuousClassification(input: List<IImageDetectionData>): ImageDetectionArrayListOutput? {
        TODO("Not yet implemented")
    }

    override suspend fun continuousClassification(
        input: Flow<IImageDetectionData>,
        scope: CoroutineScope
    ): ImageDetectionArrayListOutput? {
        TODO("Not yet implemented")
    }

    override suspend fun onStartLiveClassification(
        input: SharedFlow<IImageDetectionData>,
        scope: CoroutineScope
    ) {
        TODO("Not yet implemented")
    }
}