package com.example.driverchecker.machinelearning.imagedetection

import android.graphics.Bitmap
import com.example.driverchecker.machinelearning.data.MLMetrics
import com.example.driverchecker.machinelearning.data.MLResult
import com.example.driverchecker.machinelearning.general.local.MLLocalModel
import com.example.driverchecker.machinelearning.general.remote.MLRemoteModel
import org.pytorch.*
import org.pytorch.torchvision.TensorImageUtils

class ImageDetectionRemoteModel (private val modelPath: String? = null) :  MLRemoteModel<Bitmap, MLResult<Float>>(modelPath){
    override fun preProcess(data: Bitmap): Bitmap {
        return data
    }

    override fun evaluateData(input: Bitmap): MLResult<Float> {
        return MLResult(0F, MLMetrics())
    }

    override fun postProcess(output: MLResult<Float>): MLResult<Float> {
//        TODO("Not yet implemented")
        return output
    }
}