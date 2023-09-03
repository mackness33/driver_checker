package com.example.driverchecker.machinelearning.models.pytorch

import android.graphics.Bitmap
import android.graphics.RectF
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.data.ClassificationSupergroupMap
import com.example.driverchecker.machinelearning.data.ImageDetectionItem
import com.example.driverchecker.machinelearning.helpers.ImageDetectionUtils
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.json.Json
import org.pytorch.*
import org.pytorch.torchvision.TensorImageUtils

open class YOLOModel :
    ImageDetectionTorchModel<String>
{
    constructor() : super()

    constructor(modelPath: String? = null, classificationsJson: String? = null) : super(modelPath, classificationsJson)

    constructor(modelPath: String? = null, newClassifications: ClassificationSupergroupMap<String>? = null) : super(modelPath, newClassifications)

    // model input image size
    protected val inputWidth = 640
    protected val inputHeight = 640

    // model output is of size 25200*(num_of_class+5)
    protected val outputRow = 25200 // as decided by the YOLOv5 model for input image of size 640*640
    protected var threshold = 0.20f // score above which a detection is generated
    protected val maxPredictionsLimit = 5

    override fun preProcess(data: IImageDetectionInput): IImageDetectionInput {
        val resizedBitmap = Bitmap.createScaledBitmap(data.input, inputWidth, inputHeight, true)
        return ImageDetectionInput(resizedBitmap)
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
            output.input,
            ImageDetectionUtils.nonMaxSuppression(output.listItems, maxPredictionsLimit, threshold)
        )
    }

    open fun outputsToNMSPredictions(
        outputs: FloatArray,
        image: IImageDetectionInput
    ): IImageDetectionOutput<String> {
        val results: ClassificationItemMutableList<IImageDetectionItem<String>, String> = ClassificationItemMutableList()
//        val groupsFound: MutableMap<String, MutableSet<IClassification<String>>> = mutableMapOf()
        val (scaleX, scaleY) = image.input.width/inputWidth to image.input.height/inputHeight
        val outputColumn = mClassifier.size() + 5 // left, top, right, bottom, score and class probability

        for (i in 0 until outputRow) {
            val offset = i * outputColumn
            if (outputs[offset + 4] > threshold) {
                val (x, y, width, height) = outputs.slice(offset..offset + 3)
                var max = outputs[offset + 5]
                var clsIndex = 0
                val rect = RectF(
                    scaleX * (x - width / 2),
                    scaleY * (y - height / 2),
                    scaleX * (x + width / 2),
                    scaleY * (y + height / 2)
                )

                for (j in 0 until mClassifier.size()) {
                    if (outputs[offset + 5 + j] > max) {
                        max = outputs[offset + 5 + j]
                        clsIndex = j
                    }
                }

                results.add(
                    ImageDetectionItem(
                        clsIndex,
                        rect,
                        outputs[i * outputColumn + 4],
                        mClassifier.get(clsIndex) ?: throw Throwable("Classifier didn't find any suitable class"),
                    )
                )

//                groupsFound.merge(mClassifier.get(clsIndex)!!.supergroup, mutableSetOf(mClassifier.get(clsIndex)!!)) { newValue, oldValue ->
//                    newValue.union(oldValue).toMutableSet()
//                }
            }
        }
        return ImageDetectionOutput(
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