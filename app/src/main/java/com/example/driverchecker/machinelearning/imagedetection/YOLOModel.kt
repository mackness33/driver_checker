package com.example.driverchecker.machinelearning.imagedetection

import android.graphics.Bitmap
import android.graphics.RectF
import com.example.driverchecker.ImageDetectionUtils
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.general.IClassifier
import com.example.driverchecker.machinelearning.general.IClassifierModel
import com.example.driverchecker.machinelearning.general.MutableClassifier
import com.example.driverchecker.machinelearning.general.local.MLLocalModel
import org.pytorch.*
import org.pytorch.torchvision.TensorImageUtils
import kotlin.collections.ArrayList

open class YOLOModel (modelPath: String? = null) :
    MLLocalModel<IImageDetectionData, ImageDetectionArrayListOutput>(modelPath),
    IClassifierModel<IImageDetectionData, ImageDetectionArrayListOutput, Boolean>
{
    val _classifier = MutableClassifier<Boolean>(null)
    val classifier: IClassifier<Boolean>
        get() = _classifier


    override fun preProcess(data: IImageDetectionData): IImageDetectionData {
        val resizedBitmap = Bitmap.createScaledBitmap(data.data, inputWidth, inputHeight, true)
        return ImageDetectionBaseInput(resizedBitmap)
    }

    override fun evaluateData(input: IImageDetectionData): ImageDetectionArrayListOutput {
        // preparing input tensor
        val inputTensor: Tensor = TensorImageUtils.bitmapToFloat32Tensor(input.data,
            ImageDetectionUtils.NO_MEAN_RGB, ImageDetectionUtils.NO_STD_RGB, MemoryFormat.CHANNELS_LAST)

        // running the model
        val outputTuple: Array<IValue> = module!!.forward(IValue.from(inputTensor)).toTuple()

        // getting tensor content as java array of floats
        val predictions: FloatArray = outputTuple[0].toTensor().dataAsFloatArray

        return outputsToNMSPredictions(
                predictions,
                input
            )
    }

    override fun postProcess(output: ImageDetectionArrayListOutput): ImageDetectionArrayListOutput {
        return ImageDetectionUtils.nonMaxSuppression(output, maxPredictionsLimit, threshold)
    }

    // model input image size
    protected val sizePair = Pair(640, 640)
    protected val inputWidth = sizePair.first
    protected val inputHeight = sizePair.second

    // model output is of size 25200*(num_of_class+5)
    protected val outputRow = 25200 // as decided by the YOLOv5 model for input image of size 640*640
    protected val outputColumn = 7 // left, top, right, bottom, score and 80 class probability
    protected val threshold = 0.20f // score above which a detection is generated
    protected val maxPredictionsLimit = 5

    protected val classes: ArrayList<String>? = null

    fun loadModel(modulePath: String, classesPath: Pair<Int, String>) {
        loadModel(modulePath)

    }

    // The two methods nonMaxSuppression and IOU below are ported from https://github.com/hollance/YOLO-CoreML-MPSNNGraph/blob/master/Common/Helpers.swift
    open fun outputsToNMSPredictions(
        outputs: FloatArray,
        image: IImageDetectionData
    ): ImageDetectionArrayListOutput {
        val results: ImageDetectionArrayListOutput = ImageDetectionArrayListOutput()
        val (scaleX, scaleY) = Pair(image.data.width/inputWidth, image.data.height/inputHeight)

        for (i in 0 until outputRow) {
            if (outputs[i * outputColumn + 4] > threshold) {
                val x = outputs[i * outputColumn]
                val y = outputs[i * outputColumn + 1]
                val w = outputs[i * outputColumn + 2]
                val h = outputs[i * outputColumn + 3]

                var max = outputs[i * outputColumn + 5]
                var cls = 0
                for (j in 0..outputColumn - 5) {
                    if (outputs[i * outputColumn + 5 + j] > max) {
                        max = outputs[i * outputColumn + 5 + j]
                        cls = j
                    }
                }
                val rect = RectF(
                    scaleX * (x - w / 2),
                    scaleY * (y - h / 2),
                    scaleX * (x + w / 2),
                    scaleY * (y + h / 2)
                )
                results.add(
                    ImageDetectionOutput(
                        ImageDetectionBox(
                            cls,
                            rect
                        ),
                        outputs[i * outputColumn + 4],
                        image,
                        listOf(1)
                    )
                )
            }
        }
        return results
    }

    override fun loadClassifications(newClassifications: ClassificationSuperclassMap<Boolean>?) : Boolean {
        return _classifier.load(newClassifications)
    }
}