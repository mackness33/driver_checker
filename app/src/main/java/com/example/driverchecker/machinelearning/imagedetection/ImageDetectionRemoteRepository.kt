package com.example.driverchecker.machinelearning.imagedetection

import android.graphics.Bitmap
import com.example.driverchecker.machinelearning.data.ImageDetectionBox
import com.example.driverchecker.machinelearning.data.ImageDetectionInput
import com.example.driverchecker.machinelearning.data.MLResult
import com.example.driverchecker.machinelearning.general.local.LiveEvaluationStateInterface
import com.example.driverchecker.machinelearning.general.local.MLLocalModel
import com.example.driverchecker.machinelearning.general.local.MLLocalRepository
import com.example.driverchecker.machinelearning.general.remote.MLRemoteModel
import com.example.driverchecker.machinelearning.general.remote.MLRemoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

class ImageDetectionRemoteRepository (override val model: MLRemoteModel<ImageDetectionInput, ImageDetectionArrayResult>?) : MLRemoteRepository<ImageDetectionInput, ImageDetectionBox, ImageDetectionArrayResult>(model) {
    override suspend fun onStopLiveClassification() {
        TODO("Not yet implemented")
    }

    override val analysisProgressState: StateFlow<LiveEvaluationStateInterface<ImageDetectionArrayResult>>?
        get() = TODO("Not yet implemented")
    override val repositoryScope: CoroutineScope
        get() = TODO("Not yet implemented")

    override suspend fun continuousClassification(input: List<ImageDetectionInput>): ImageDetectionArrayResult? {
        TODO("Not yet implemented")
    }

    override suspend fun continuousClassification(
        input: Flow<ImageDetectionInput>,
        scope: CoroutineScope
    ): ImageDetectionArrayResult? {
        TODO("Not yet implemented")
    }

    override suspend fun onStartLiveClassification(
        input: SharedFlow<ImageDetectionInput>,
        scope: CoroutineScope
    ) {
        TODO("Not yet implemented")
    }
}