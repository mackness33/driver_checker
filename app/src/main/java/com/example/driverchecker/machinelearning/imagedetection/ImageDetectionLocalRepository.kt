package com.example.driverchecker.machinelearning.imagedetection

import android.graphics.Bitmap
import com.example.driverchecker.machinelearning.data.MLMetrics
import com.example.driverchecker.machinelearning.data.MLResult
import com.example.driverchecker.machinelearning.general.local.MLLocalModel
import com.example.driverchecker.machinelearning.general.local.MLLocalRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.map

class ImageDetectionLocalRepository (override val model: MLLocalModel<Bitmap, MLResult<Float>>?) : MLLocalRepository<Bitmap, MLResult<Float>> (model) {
    private class ImageDetectionLocalWindow () : MLWindow<MLResult<Float>>() {
        override fun calculate(element: MLResult<Float>) {
            confidence += 5F
            partialResult = MLResult<Float> (confidence)
        }
    }

    override suspend fun continuousClassification(input: List<Bitmap>): MLResult<Float>? {
//        TODO("Not yet implemented")
//        return null
//        val inputAsFlow: Flow<Bitmap> =  input.asFlow().map { bitmap -> Bitmap.createScaledBitmap(bitmap, 299, (bitmap.height*299)/bitmap.width, true) }
        val scaledInput: List<Bitmap> =  input.map { bitmap -> Bitmap.createScaledBitmap(bitmap, 299, (bitmap.height*299)/bitmap.width, true) }
        return classificationThroughWindow(scaledInput, ImageDetectionLocalWindow())
    }
}