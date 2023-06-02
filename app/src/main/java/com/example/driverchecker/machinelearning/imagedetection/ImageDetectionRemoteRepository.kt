package com.example.driverchecker.machinelearning.imagedetection

import android.graphics.Bitmap
import com.example.driverchecker.machinelearning.data.MLResult
import com.example.driverchecker.machinelearning.general.local.MLLocalModel
import com.example.driverchecker.machinelearning.general.local.MLLocalRepository
import com.example.driverchecker.machinelearning.general.remote.MLRemoteModel
import com.example.driverchecker.machinelearning.general.remote.MLRemoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

class ImageDetectionRemoteRepository (override val model: MLRemoteModel<Bitmap, MLResult<Float>>?) : MLRemoteRepository<Bitmap, MLResult<Float>>(model) {
    override suspend fun continuousClassification(input: List<Bitmap>): MLResult<Float>? {
        TODO("Not yet implemented")
    }

    override suspend fun continuousClassification(input: Flow<Bitmap>, scope: CoroutineScope): MLResult<Float>? {
        TODO("Not yet implemented")
    }

    override suspend fun onStartLiveClassification(
        input: SharedFlow<Bitmap>,
        scope: CoroutineScope
    ) {
        TODO("Not yet implemented")
    }

    override suspend fun onStopLiveClassification() {
        TODO("Not yet implemented")
    }
}