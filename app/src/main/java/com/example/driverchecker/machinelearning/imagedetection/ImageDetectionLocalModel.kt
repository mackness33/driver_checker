package com.example.driverchecker.machinelearning.imagedetection

import android.graphics.Bitmap
import com.example.driverchecker.machinelearning.data.MLMetrics
import com.example.driverchecker.machinelearning.data.MLResult
import com.example.driverchecker.machinelearning.general.local.MLLocalModel
import org.pytorch.*
import org.pytorch.torchvision.TensorImageUtils

class ImageDetectionLocalModel (private val modelPath: String? = null) :  MLLocalModel<Bitmap, MLResult<Float>>(modelPath){
    override fun preProcess(data: Bitmap): Bitmap {
//        return Bitmap.createScaledBitmap(data, 500, (data.height*500)/data.width, true)
        return Bitmap.createScaledBitmap(data, 299, 299, true)
    }

    override fun evaluateData(input: Bitmap): MLResult<Float> {
        // preparing input tensor
        val inputTensor: Tensor = TensorImageUtils.bitmapToFloat32Tensor(input,
            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB, MemoryFormat.CHANNELS_LAST)

        // running the model
        val outputTensor: Tensor = module!!.forward(IValue.from(inputTensor)).toTensor()

        // getting tensor content as java array of floats
        val scores: FloatArray = outputTensor.dataAsFloatArray

        // searching for the index with maximum score
        var maxScore: Float = -Float.MAX_VALUE
        var maxScoreIdx: Int = -1
        var index = 0
        for (iter in scores.iterator()) {
            if (iter > maxScore) {
                maxScore = iter
                maxScoreIdx = index
            }
            index++
        }

        return MLResult(maxScore, MLMetrics())
    }

    override fun postProcess(output: MLResult<Float>): MLResult<Float> {
//        TODO("Not yet implemented")
        return output
    }
}