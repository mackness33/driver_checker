package com.example.driverchecker

import android.graphics.Bitmap
import org.pytorch.*
import org.pytorch.torchvision.TensorImageUtils

//class ImageDetectionModel : PyModel<Bitmap>() {
//    override fun preProcess(data: Bitmap): Bitmap {
//        return data
//    }
//
//    override fun evaluateData(data: Bitmap): String {
//        // preparing input tensor
//        val inputTensor: Tensor = TensorImageUtils.bitmapToFloat32Tensor(data,
//            TensorImageUtils.TORCHVISION_NORM_MEAN_RGB, TensorImageUtils.TORCHVISION_NORM_STD_RGB, MemoryFormat.CHANNELS_LAST)
//
//        // running the model
//        val outputTensor: Tensor = module!!.forward(IValue.from(inputTensor)).toTensor()
//
//        // getting tensor content as java array of floats
//        val scores: FloatArray = outputTensor.dataAsFloatArray
//
//        // searching for the index with maximum score
//        var maxScore: Float = -Float.MAX_VALUE
//        var maxScoreIdx: Int = -1
//        var index = 0
//        for (iter in scores.iterator()) {
//            if (iter > maxScore) {
//                maxScore = iter
//                maxScoreIdx = index
//            }
//            index++
//        }
//
//        return maxScore.toString()
//    }
//}