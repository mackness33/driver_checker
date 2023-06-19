package com.example.driverchecker.machinelearning.imagedetection

import android.graphics.Bitmap
import android.graphics.RectF
import com.example.driverchecker.ImageDetectionUtils
import com.example.driverchecker.machinelearning.data.ImageDetectionBox
import com.example.driverchecker.machinelearning.data.ImageDetectionInput
import com.example.driverchecker.machinelearning.data.MLResult
import com.example.driverchecker.machinelearning.general.local.MLLocalModel
import org.pytorch.*
import org.pytorch.torchvision.TensorImageUtils
import java.util.*
import kotlin.collections.ArrayList

typealias ImageDetectionResult = MLResult<ImageDetectionBox>
typealias ImageDetectionArrayResult = ArrayList<ImageDetectionResult>

open class YOLOModel (private val modelPath: String? = null) :  MLLocalModel<ImageDetectionInput, ImageDetectionArrayResult>(modelPath){
    override fun preProcess(data: ImageDetectionInput): ImageDetectionInput {
        val resizedBitmap = Bitmap.createScaledBitmap(data.image, inputWidth, inputHeight, true)
        return ImageDetectionInput(resizedBitmap, data.scale, data.vector, data.start)
    }

    override fun evaluateData(input: ImageDetectionInput): ImageDetectionArrayResult {
        // preparing input tensor
        val inputTensor: Tensor = TensorImageUtils.bitmapToFloat32Tensor(input.image,
            ImageDetectionUtils.NO_MEAN_RGB, ImageDetectionUtils.NO_STD_RGB, MemoryFormat.CHANNELS_LAST)

        // running the model
        val outputTuple: Array<IValue> = module!!.forward(IValue.from(inputTensor)).toTuple()

        // getting tensor content as java array of floats
        val predictions: FloatArray = outputTuple[0].toTensor().dataAsFloatArray

        return outputsToNMSPredictions(
                predictions,
                input.scale,
                input.vector,
                input.start,
                input.image
            )
    }

    override fun postProcess(output: ImageDetectionArrayResult): ImageDetectionArrayResult {
        return ImageDetectionUtils.nonMaxSuppression(output, maxPredictionsLimit, threshold)
    }

    // model input image size
    protected val inputWidth = 640
    protected val inputHeight = 640

    // model output is of size 25200*(num_of_class+5)
    protected val outputRow = 25200 // as decided by the YOLOv5 model for input image of size 640*640
    protected val outputColumn = 5 // left, top, right, bottom, score and 80 class probability
    protected val threshold = 0.10f // score above which a detection is generated
    protected val maxPredictionsLimit = 5

    protected val classes: ArrayList<String>? = null

    fun loadModel(modulePath: String, classesPath: Pair<Int, String>) {
        loadModel(modulePath)

    }

    // The two methods nonMaxSuppression and IOU below are ported from https://github.com/hollance/YOLO-CoreML-MPSNNGraph/blob/master/Common/Helpers.swift
    open fun outputsToNMSPredictions(
        outputs: FloatArray,
        scale: Pair<Float, Float>,
        vector: Pair<Float, Float>,
        start: Pair<Float, Float>,
        image: Bitmap
    ): ImageDetectionArrayResult {
        val results: ArrayList<MLResult<ImageDetectionBox>> = ArrayList()
        for (i in 0 until outputRow) {
            if (outputs[i * outputColumn + 4] > threshold) {
                val x = outputs[i * outputColumn]
                val y = outputs[i * outputColumn + 1]
                val w = outputs[i * outputColumn + 2]
                val h = outputs[i * outputColumn + 3]

                var max = outputs[i * outputColumn + 5]
                var cls = 0
                for (j in 0 until outputColumn - 5) {
                    if (outputs[i * outputColumn + 5 + j] > max) {
                        max = outputs[i * outputColumn + 5 + j]
                        cls = j
                    }
                }
                val rect = RectF(
                    scale.first * (x - w / 2),
                    scale.second * (y - h / 2),
                    scale.first * (x + w / 2),
                    scale.second * (y + h / 2)
                )
                val result = ImageDetectionBox(
                    cls,
                    rect
                )
                results.add(MLResult(
                    ImageDetectionBox(
                        cls,
                        rect
                    ),
                    outputs[i * outputColumn + 4],
                    listOf(1),
                    image
                ))
            }
        }
        return results
    }
}

data class ModelClasses (val name: String, val index: Int, ) {

}