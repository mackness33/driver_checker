package com.example.driverchecker.machinelearning.imagedetection

import android.graphics.Bitmap
import android.graphics.RectF
import com.example.driverchecker.machinelearning.data.*
import java.util.*
import kotlin.math.max


interface ImageDetectionGeneralModel {
    // model input image size
    var mInputWidth: Int
    var mInputHeight: Int

//    var mClasses: Array<String> = TODO()
    fun buildInput (bitmap: Bitmap, container: ResultContainer? = null) : ImageDetectionInput {
//        val imgScaleX: Float = (bitmap.width / mInputWidth).toFloat()
//        val imgScaleY: Float = (bitmap.height / mInputHeight).toFloat()

        val transformations: ImageDetectionTransformations = when {
            // TODO: can remove the second condition
            container == null || container.size == Pair(0.0f, 0.0f)-> ImageDetectionTransformations(
                scale = Pair((bitmap.width / mInputWidth).toFloat(), (bitmap.height / mInputHeight).toFloat())
            )
            else -> {
                val imageVector: Pair<Float, Float> = Pair(container.size.first / bitmap.width, container.size.second / bitmap.height)
                ImageDetectionTransformations(
                    scale = Pair(
                        (bitmap.width / mInputWidth).toFloat(),
                        (bitmap.height / mInputHeight).toFloat()
                    ),
                    vector = imageVector,
                    start = Pair(
                        (container.size.first - imageVector.first * container.size.first) / 2,
                        (container.size.second - imageVector.second * container.size.second) / 2,
                    )
                )
            }
        }
//        val ivScaleX: Float = if (container.size != null) container.size.first / bitmap.width else 1.0f
//        val ivScaleY: Float = if (container.size != null) container.size.second / bitmap.height else 1.0f
//        val startX: Float = if (container.offset != null) (container_offset.first - ivScaleX * bitmap.width) / 2 else 0.0f
//        val startY: Float = if (container_offset != null) (container_offset.second - ivScaleY * bitmap.height) / 2 else 0.0f


        return ImageDetectionInput(
            bitmap,
            transformations
        )
    }

    // The two methods nonMaxSuppression and IOU below are ported from https://github.com/hollance/YOLO-CoreML-MPSNNGraph/blob/master/Common/Helpers.swift
    /**
     * Removes bounding boxes that overlap too much with other boxes that have
     * a higher score.
     * - Parameters:
     * - boxes: an array of bounding boxes and their scores
     * - limit: the maximum number of boxes that will be selected
     * - threshold: used to decide whether boxes overlap too much
     */
     fun nonMaxSuppression(
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
                if (selected.size >= limit) break
                for (j in i + 1 until boxes.size) {
                    if (active[j]) {
                        val boxB = boxes[j]
                        val iou = intersectionOverUnion(boxA.rect, boxB.rect)
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
    fun intersectionOverUnion(a: RectF, b: RectF): Float {
        val areaA: Float = ((a.right - a.left) * (a.bottom - a.top))
        if (areaA <= 0.0) return 0.0f

        val areaB: Float = ((b.right - b.left) * (b.bottom - b.top))
        if (areaB <= 0.0) return 0.0f

        val intersectionMinX: Float = max(a.left, b.left)
        val intersectionMinY: Float = max(a.top, b.top)
        val intersectionMaxX: Float = kotlin.math.min(a.right, b.right)
        val intersectionMaxY: Float = kotlin.math.min(a.bottom, b.bottom)
        val intersectionArea = max(intersectionMaxY - intersectionMinY, 0f) *
                max(intersectionMaxX - intersectionMinX, 0f)
        return intersectionArea / (areaA + areaB - intersectionArea)
    }
}