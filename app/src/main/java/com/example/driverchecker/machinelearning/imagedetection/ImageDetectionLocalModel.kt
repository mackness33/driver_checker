package com.example.driverchecker.machinelearning.imagedetection

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.Rect
import android.graphics.RectF
import com.example.driverchecker.machinelearning.data.ImageDetectionBox
import com.example.driverchecker.machinelearning.data.ImageDetectionInput
import com.example.driverchecker.machinelearning.data.MLResult
import com.example.driverchecker.machinelearning.general.local.MLLocalModel
import org.pytorch.*
import org.pytorch.torchvision.TensorImageUtils
import java.util.*
import kotlin.math.max


class ImageDetectionLocalModel (private val modelPath: String? = null) :  MLLocalModel<ImageDetectionInput, MLResult<ArrayList<ImageDetectionBox>>>(modelPath){
    override fun preProcess(data: ImageDetectionInput): ImageDetectionInput {
        val resizedBitmap = Bitmap.createScaledBitmap(data.image, mInputWidth, mInputHeight, true)
        return ImageDetectionInput(resizedBitmap, data.scale, data.vector, data.start)
    }

    override fun evaluateData(input: ImageDetectionInput): MLResult<ArrayList<ImageDetectionBox>> {
        // preparing input tensor
        val inputTensor: Tensor = TensorImageUtils.bitmapToFloat32Tensor(input.image,
            NO_MEAN_RGB, NO_STD_RGB, MemoryFormat.CHANNELS_LAST)

        // running the model
        val outputTuple: Array<IValue> = module!!.forward(IValue.from(inputTensor)).toTuple()
        val outputTensor = outputTuple[0].toTensor()
//        val outputTensor: Tensor = module!!.forward(IValue.from(inputTensor)).toTensor()

        val dtype = outputTensor.dtype()
        val shape = outputTensor.shape()
        val numel = outputTensor.numel()
        val memoryFormat: MemoryFormat = outputTensor.memoryFormat()
        // getting tensor content as java array of floats
        val scores: FloatArray = outputTensor.dataAsFloatArray

        return MLResult(outputsToNMSPredictions(scores, input.scale, input.vector, input.start))
    }

    override fun postProcess(output: MLResult<ArrayList<ImageDetectionBox>>): MLResult<ArrayList<ImageDetectionBox>> {
//        TODO("Not yet implemented")
        val resSuppressed = nonMaxSuppression(output.result, threshold = mThreshold)
        return MLResult(resSuppressed, output.metrics)
    }

    companion object {
        // for yolov5 model, no need to apply MEAN and STD
        var NO_MEAN_RGB = floatArrayOf(0.0f, 0.0f, 0.0f)
        var NO_STD_RGB = floatArrayOf(1.0f, 1.0f, 1.0f)
    }

    // model input image size
    var mInputWidth = 640
        protected set
    var mInputHeight = 640
        protected set

    // model output is of size 25200*(num_of_class+5)
    private val mOutputRow = 25200 // as decided by the YOLOv5 model for input image of size 640*640
    private val mOutputColumn = 7 // left, top, right, bottom, score and 80 class probability
    private val mThreshold = 0.10f // score above which a detection is generated
    private val mNmsLimit = 5

//    var mClasses: Array<String> = TODO()

    // The two methods nonMaxSuppression and IOU below are ported from https://github.com/hollance/YOLO-CoreML-MPSNNGraph/blob/master/Common/Helpers.swift
    /**
     * Removes bounding boxes that overlap too much with other boxes that have
     * a higher score.
     * - Parameters:
     * - boxes: an array of bounding boxes and their scores
     * - limit: the maximum number of boxes that will be selected
     * - threshold: used to decide whether boxes overlap too much
     */
    open fun nonMaxSuppression(
        boxes: ArrayList<ImageDetectionBox>,
        limit: Int = Int.MAX_VALUE,
        threshold: Float
    ): ArrayList<ImageDetectionBox> {

        // Do an argsort on the confidence scores, from high to low.
        boxes.sortWith(Comparator { o1, o2 -> o2.score.compareTo(o1.score) })
        val selected: ArrayList<ImageDetectionBox> = ArrayList()
        val active = BooleanArray(boxes.size)
        Arrays.fill(active, true)
        var numActive = active.size

        // The algorithm is simple: Start with the box that has the highest score.
        // Remove any remaining boxes that overlap it more than the given threshold
        // amount. If there are any boxes left (i.e. these did not overlap with any
        // previous boxes), then repeat this procedure, until no more boxes remain
        // or the limit has been reached.
        var done = false
        var i = 0
        while (i < boxes.size && !done) {
            if (active[i]) {
                val boxA = boxes[i]
                selected.add(boxA)
                if (selected.size >= mNmsLimit) break
                for (j in i + 1 until boxes.size) {
                    if (active[j]) {
                        val boxB = boxes[j]
                        val iou = IOU(boxA.rect, boxB.rect)
                        if (iou > threshold) {
                            active[j] = false
                            numActive -= 1
                            if (numActive <= 0) {
                                done = true
                                break
                            }
                        }
                    }
                }
            }
            i++
        }
        return selected
    }

    /**
     * Computes intersection-over-union overlap between two bounding boxes.
     */
    open fun IOU(a: RectF, b: RectF): Float {
        val areaA: Float = ((a.right - a.left) * (a.bottom - a.top)).toFloat()
        if (areaA <= 0.0) return 0.0f

        val areaB: Float = ((b.right - b.left) * (b.bottom - b.top)).toFloat()
        if (areaB <= 0.0) return 0.0f

        val intersectionMinX: Float = max(a.left, b.left).toFloat()
        val intersectionMinY: Float = max(a.top, b.top).toFloat()
        val intersectionMaxX: Float = kotlin.math.min(a.right, b.right).toFloat()
        val intersectionMaxY: Float = kotlin.math.min(a.bottom, b.bottom).toFloat()
        val intersectionArea = max(intersectionMaxY - intersectionMinY, 0f) *
                max(intersectionMaxX - intersectionMinX, 0f)
        return intersectionArea / (areaA + areaB - intersectionArea)
    }

    open fun outputsToNMSPredictions(
        outputs: FloatArray,
        scale: Pair<Float, Float>,
        vector: Pair<Float, Float>,
        start: Pair<Float, Float>
    ): ArrayList<ImageDetectionBox> {
        val results: ArrayList<ImageDetectionBox> = ArrayList()
        for (i in 0 until mOutputRow) {
            if (outputs[i * mOutputColumn + 4] > mThreshold) {
                val x = outputs[i * mOutputColumn]
                val y = outputs[i * mOutputColumn + 1]
                val w = outputs[i * mOutputColumn + 2]
                val h = outputs[i * mOutputColumn + 3]
                val left = scale.first * (x - w / 2)
                val top = scale.second * (y - h / 2)
                val right = scale.first * (x + w / 2)
                val bottom = scale.second * (y + h / 2)
                var max = outputs[i * mOutputColumn + 5]
                var cls = 0
                for (j in 0 until mOutputColumn - 5) {
                    if (outputs[i * mOutputColumn + 5 + j] > max) {
                        max = outputs[i * mOutputColumn + 5 + j]
                        cls = j
                    }
                }
                val rect = RectF(
                    start.first + vector.first * scale.first * (x - w / 2),
                    start.second + scale.second * (y - h / 2) * vector.second,
                    start.first + vector.first * scale.first * (x + w / 2),
                    start.second + vector.second * scale.second * (y + h / 2)
                )
                val result = ImageDetectionBox(
                    cls,
                    outputs[i * mOutputColumn + 4],
                    rect
                )
                results.add(result)
            }
        }
        return results
    }
}