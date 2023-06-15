package com.example.driverchecker

import android.graphics.RectF
import com.example.driverchecker.machinelearning.imagedetection.ImageDetectionArrayResult
import com.example.driverchecker.machinelearning.imagedetection.ImageDetectionResult
import java.util.*
import kotlin.Comparator
import kotlin.collections.ArrayList
import kotlin.math.max

object ImageDetectionUtils {
    val NO_MEAN_RGB = floatArrayOf(0.0f, 0.0f, 0.0f)
    val NO_STD_RGB = floatArrayOf(1.0f, 1.0f, 1.0f)

    /**
     * Removes bounding boxes that overlap too much with other boxes that have
     * a higher score.
     * - Parameters:
     * - boxes: an array of bounding boxes and their scores
     * - limit: the maximum number of boxes that will be selected
     * - threshold: used to decide whether boxes overlap too much
     */
    fun nonMaxSuppression(
        boxes: ImageDetectionArrayResult,
        limit: Int = Int.MAX_VALUE,
        threshold: Float
    ): ImageDetectionArrayResult {

        // Do an argsort on the confidence scores, from high to low.
        boxes.sortedWith { o1, o2 -> o2.confidence.compareTo(o1.confidence) }
        val selected: ArrayList<ImageDetectionResult> = ArrayList()
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
                        val iou = intersactionOverUnion(boxA.result.rect, boxB.result.rect)
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
    fun intersactionOverUnion(a: RectF, b: RectF): Float {
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
}