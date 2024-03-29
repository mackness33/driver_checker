package com.example.driverchecker.machinelearning.models.pytorch

import android.graphics.RectF
import android.util.Log
import com.example.driverchecker.machinelearning.collections.MutableClassificationOutput
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.data.ImageDetectionFullItem
import com.example.driverchecker.machinelearning.helpers.ImageDetectionUtils
import kotlinx.coroutines.CoroutineScope
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.pytorch.*
import org.pytorch.torchvision.TensorImageUtils

class YOLOModel :
    ClassifierTorchModel<IImageDetectionInput, IImageDetectionOutput<String>, String>
{
    constructor(scope: CoroutineScope) : super(scope)

    constructor(modelPath: String? = null, classificationsJson: String? = null, scope: CoroutineScope) : super(modelPath, classificationsJson, scope) {
        modelStateProducer.initialize()
        initModel(modelPath)
        initClassifier(classificationsJson)
    }

    constructor(modelPath: String? = null, newClassifications: Map<String, Set<IClassification<String>>>? = null, scope: CoroutineScope) : super(modelPath, newClassifications, scope)

    // model input image size
    private val inputWidth = 640
    private val inputHeight = 640

    // model output is of size 25200*(num_of_class+5)
    private val outputRow = 25200 // as decided by the YOLOv5 model for input image of size 640*640
    private val maxPredictionsLimit = 5

    override fun preProcess(data: IImageDetectionInput): IImageDetectionInput {
        val clone = data.copy()
        clone.resizeImage(inputWidth, inputHeight)
        return clone
    }

    override fun evaluateData(input: IImageDetectionInput): IImageDetectionOutput<String> {
        // preparing input tensor
        val inputTensor: Tensor = TensorImageUtils.bitmapToFloat32Tensor(input.input,
            ImageDetectionUtils.NO_MEAN_RGB, ImageDetectionUtils.NO_STD_RGB, MemoryFormat.CHANNELS_LAST)

        // running the model
        val outputTuple: Array<IValue> = module!!.forward(IValue.from(inputTensor)).toTuple()

        // getting tensor content as java array of floats
        val predictions: FloatArray = outputTuple[0].toTensor().dataAsFloatArray

        return outputsToNMSPredictions(predictions, input)
    }

    override fun postProcess(output: IImageDetectionOutput<String>): IImageDetectionOutput<String> {
        return ImageDetectionOutput(
            ImageDetectionUtils.nonMaxSuppression(output, maxPredictionsLimit, threshold)
        )
    }

    private fun outputsToNMSPredictions(
        outputs: FloatArray,
        image: IImageDetectionInput
    ): IImageDetectionOutput<String> {
        val results: IMutableClassificationOutput<IImageDetectionItem<String>, String> = MutableClassificationOutput(image.index)
        val outputColumn = mClassifier.size() + 5 // left, top, right, bottom, score and class probability
        val imageScaleX = image.imageRatio.first
        val imageScaleY = image.imageRatio.second
        val imageRect = RectF(
            0.0f,
            0.0f,
            inputWidth.toFloat(),
            inputHeight.toFloat()
        )

        for (i in 0 until outputRow) {
            val offset = i * outputColumn
            if (outputs[offset + 4] > threshold) {
                val (x, y, width, height) = outputs.slice(offset..offset + 3)

                var max = outputs[offset + 5]
                var clsIndex = 0

                val rect = RectF(
                    imageScaleX * (x - width / 2),
                    imageScaleY * (y - height / 2),
                    imageScaleX * (x + width / 2),
                    imageScaleY * (y + height / 2)
                )

                if (!imageRect.contains(rect)) continue

//                val s = max(outputs.slice(offset + 5 until offset + 5 + mClassifier.size()))

                for (j in 0 until mClassifier.size()) {
                    if (outputs[offset + 5 + j] > max) {
                        max = outputs[offset + 5 + j]
                        clsIndex = j
                    }
                }

                results.push(
                    ImageDetectionFullItem(
                        clsIndex,

                        rect,
                        outputs[i * outputColumn + 4],
                        mClassifier.get(clsIndex) ?: throw Throwable("Classifier didn't find any suitable class"),
                    )
                )
            }
        }

        return ImageDetectionOutput(results)
    }

    override fun loadClassifications(json: String?): Boolean {
        if (json.isNullOrBlank()) {
            modelStateProducer.classificationReady(false)
            return false
        }

        try {
            // TODO: For now ImportClassifier can "understand" only String for simplicity
            val importedJson = Json.decodeFromString<ImportClassifier<String>>(json)
            val result = mClassifier.load(importedJson)
            modelStateProducer.classificationReady(result)
        } catch (e : Throwable) {
            Log.e("ClassifierTorchModel", e.message.toString(), e)
            modelStateProducer.classificationReady(false)
        }

        return true
    }

}