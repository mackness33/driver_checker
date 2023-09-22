package com.example.driverchecker.machinelearning.models.pytorch

import android.graphics.Bitmap
import android.graphics.RectF
import com.example.driverchecker.machinelearning.collections.ClassificationItemMutableList
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.data.ImageDetectionFullItem
import com.example.driverchecker.machinelearning.helpers.ImageDetectionUtils
import com.example.driverchecker.utils.BitmapUtils
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.pytorch.*
import org.pytorch.torchvision.TensorImageUtils

class YOLOModel :
    ImageDetectionTorchModel<String>
{
    constructor() : super()

    constructor(modelPath: String? = null, classificationsJson: String? = null) : super(modelPath, classificationsJson)

    constructor(modelPath: String? = null, newClassifications: Map<String, Set<IClassification<String>>>? = null) : super(modelPath, newClassifications)

    // model input image size
    private val inputWidth = 640
    private val inputHeight = 640

    // model output is of size 25200*(num_of_class+5)
    private val outputRow = 25200 // as decided by the YOLOv5 model for input image of size 640*640
    private val maxPredictionsLimit = 5

    override fun preProcess(data: IImageDetectionInput): IImageDetectionInput {
        val resizedBitmap = Bitmap.createScaledBitmap(data.input, inputWidth, inputHeight, true)
        val rotatedBitmap: Bitmap = BitmapUtils.rotateBitmap(resizedBitmap, -90f)
        return ImageDetectionInput(rotatedBitmap)
    }

    override fun evaluateData(input: IImageDetectionInput): IImageDetectionFullOutput<String> {
        // preparing input tensor
        val inputTensor: Tensor = TensorImageUtils.bitmapToFloat32Tensor(input.input,
            ImageDetectionUtils.NO_MEAN_RGB, ImageDetectionUtils.NO_STD_RGB, MemoryFormat.CHANNELS_LAST)

        // running the model
        val outputTuple: Array<IValue> = module!!.forward(IValue.from(inputTensor)).toTuple()

        // getting tensor content as java array of floats
        val predictions: FloatArray = outputTuple[0].toTensor().dataAsFloatArray

        return outputsToNMSPredictions(predictions, input)
    }

    override fun postProcess(output: IImageDetectionFullOutput<String>): IImageDetectionFullOutput<String> {
        return ImageDetectionFullOutput(
            output.input,
            ImageDetectionUtils.nonMaxSuppression(output.listItems, maxPredictionsLimit, threshold)
        )
    }

    fun outputsToNMSPredictions(
        outputs: FloatArray,
        image: IImageDetectionInput
    ): IImageDetectionFullOutput<String> {
        val results: ClassificationItemMutableList<IImageDetectionFullItem<String>, String> = ClassificationItemMutableList()
        val (scaleX, scaleY) = image.input.width/inputWidth to image.input.height/inputHeight
        val outputColumn = mClassifier.size() + 5 // left, top, right, bottom, score and class probability

        for (i in 0 until outputRow) {
            val offset = i * outputColumn
            if (outputs[offset + 4] > threshold) {
                val (x, y, width, height) = outputs.slice(offset..offset + 3)
                var max = outputs[offset + 5]
                var clsIndex = 0
                val rect = RectF(
                    x - width / 2,
                    y - height / 2,
                    x + width / 2,
                    y + height / 2
                )
//                val rect = RectF(
//                    x,
//                    y,
//                     width,
//                    height
//                )

                for (j in 0 until mClassifier.size()) {
                    if (outputs[offset + 5 + j] > max) {
                        max = outputs[offset + 5 + j]
                        clsIndex = j
                    }
                }

                results.add(
                    ImageDetectionFullItem(
                        clsIndex,
                        rect,
                        outputs[i * outputColumn + 4],
                        mClassifier.get(clsIndex) ?: throw Throwable("Classifier didn't find any suitable class"),
                    )
                )
            }
        }

        return ImageDetectionFullOutput(
            image,
            results
        )
    }

    override fun loadClassifications(json: String?): Boolean {
        if (json.isNullOrBlank())
            return false

        // TODO: For now ImportClassifier can "understand" only String for simplicity
        val importedJson = Json.decodeFromString<ImportClassifier<String>>(json)

        return mClassifier.load(importedJson)
    }

}