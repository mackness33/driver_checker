package com.example.driverchecker.machinelearning.imagedetection

import android.graphics.Bitmap
import com.example.driverchecker.machinelearning.data.MLResult
import com.example.driverchecker.machinelearning.general.local.MLLocalModel
import com.example.driverchecker.machinelearning.general.remote.MLRemoteModel
import org.pytorch.*
import org.pytorch.torchvision.TensorImageUtils

class ImageDetectionRemoteModel (private val modelPath: String? = null) :  MLRemoteModel<Bitmap, MLResult>(modelPath){
    override fun preProcess(data: Bitmap): Bitmap {
        return data
    }

    override fun evaluateData(input: Bitmap): MLResult {
        return MLResult("nothing")
    }

    override fun postProcess(output: MLResult): MLResult {
//        TODO("Not yet implemented")
        return output
    }
}