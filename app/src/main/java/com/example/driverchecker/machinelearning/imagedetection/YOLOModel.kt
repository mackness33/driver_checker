package com.example.driverchecker.machinelearning.imagedetection

import android.graphics.Bitmap
import android.graphics.RectF
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.general.IClassifier
import com.example.driverchecker.machinelearning.general.IClassifierModel
import com.example.driverchecker.machinelearning.general.MutableClassifier
import com.example.driverchecker.machinelearning.general.local.MLLocalModel
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.pytorch.*
import org.pytorch.torchvision.TensorImageUtils

open class YOLOModel (modelPath: String? = null) :
    MLLocalModel<IImageDetectionData, ImageDetectionArrayListOutput<String>>(modelPath),
    IClassifierModel<IImageDetectionData, ImageDetectionArrayListOutput<String>, String>
{
    constructor(modelPath: String? = null, classificationsJson: String? = null) : this(modelPath) {
        loadClassifications(classificationsJson)
    }

    constructor(modelPath: String? = null, newClassifications: ClassificationSuperclassMap<String>? = null) : this(modelPath) {
        loadClassifications(newClassifications)
    }

    protected val _classifier = MutableClassifier<String>(null)
    val classifier: IClassifier<String>
        get() = _classifier

    // model input image size
    protected val inputWidth = 640
    protected val inputHeight = 640

    // model output is of size 25200*(num_of_class+5)
    protected val outputRow = 25200 // as decided by the YOLOv5 model for input image of size 640*640
    protected var threshold = 0.20f // score above which a detection is generated
    protected val maxPredictionsLimit = 5

    override fun preProcess(data: IImageDetectionData): IImageDetectionData {
        val resizedBitmap = Bitmap.createScaledBitmap(data.data, inputWidth, inputHeight, true)
        return ImageDetectionBaseInput(resizedBitmap)
    }

    override fun evaluateData(input: IImageDetectionData): ImageDetectionArrayListOutput<String> {
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

    override fun postProcess(output: ImageDetectionArrayListOutput<String>): ImageDetectionArrayListOutput<String> {
        return ImageDetectionUtils.nonMaxSuppression(output, maxPredictionsLimit, threshold)
    }

    // The two methods nonMaxSuppression and IOU below are ported from https://github.com/hollance/YOLO-CoreML-MPSNNGraph/blob/master/Common/Helpers.swift
    open fun outputsToNMSPredictions(
        outputs: FloatArray,
        image: IImageDetectionData
    ): ImageDetectionArrayListOutput<String> {
        val results: ImageDetectionArrayListOutput<String> = ImageDetectionArrayListOutput()
        val (scaleX, scaleY) = image.data.width/inputWidth to image.data.height/inputHeight
        val outputColumn = _classifier.size() + 5 // left, top, right, bottom, score and class probability

        for (i in 0 until outputRow) {
            val offset = i * outputColumn
            if (outputs[offset + 4] > threshold) {
                val (x, y, width, height) = outputs.slice(offset..offset + 3)
                var max = outputs[offset]
                var clsIndex = 0
                for (j in 0 until _classifier.size()) {
                    if (outputs[offset + 5 + j] > max) {
                        max = outputs[offset + 5 + j]
                        clsIndex = j
                    }
                }
                val rect = RectF(
                    scaleX * (x - width / 2),
                    scaleY * (y - height / 2),
                    scaleX * (x + width / 2),
                    scaleY * (y + height / 2)
                )
                results.add(
                    ImageDetectionOutput(
                        ImageDetectionBox(
                            clsIndex,
                            rect
                        ),
                        outputs[i * outputColumn + 4],
                        image,
                        _classifier.get(clsIndex) ?: throw Throwable("Classifier didn't find any suitable class")
                    )
                )
            }
        }
        return results
    }

    override fun loadClassifications(newClassifications: ClassificationSuperclassMap<String>?) : Boolean {
        return _classifier.load(newClassifications)
    }

    override fun loadClassifications(json: String?) : Boolean {
        if (json.isNullOrBlank())
            return false

        val importedJson = Json.decodeFromString<BaseClassifier<String>>(json)
        val res = _classifier.load(importedJson)

        return res
    }
}