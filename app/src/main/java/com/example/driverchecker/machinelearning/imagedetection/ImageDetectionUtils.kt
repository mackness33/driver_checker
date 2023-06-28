package com.example.driverchecker.machinelearning.imagedetection

import android.graphics.*
import androidx.camera.core.ImageProxy
import com.example.driverchecker.machinelearning.data.ImageDetectionArrayListOutput
import java.io.ByteArrayOutputStream
import java.util.*
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
    fun <Superclass> nonMaxSuppression(
        boxes: ImageDetectionArrayListOutput<Superclass>,
        limit: Int = Int.MAX_VALUE,
        threshold: Float
    ): ImageDetectionArrayListOutput<Superclass> {

        // Do an argument sort on the confidence scores, from high to low.
        boxes.sortWith { o1, o2 -> o2.confidence.compareTo(o1.confidence) }
        val selected: ImageDetectionArrayListOutput<Superclass> = ImageDetectionArrayListOutput(10)
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
                        val iou = intersectionOverUnion(boxA.result.rect, boxB.result.rect)
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

    fun imageProxyToBitmap(image: ImageProxy): Bitmap {
        val yBuffer = image.planes[0].buffer // Y
        val vuBuffer = image.planes[2].buffer // VU

        val ySize = yBuffer.remaining()
        val vuSize = vuBuffer.remaining()

        val nv21 = ByteArray(ySize + vuSize)

        yBuffer.get(nv21, 0, ySize)
        vuBuffer.get(nv21, ySize, vuSize)

        val yuvImage = YuvImage(nv21, ImageFormat.NV21, image.width, image.height, null)
        val out = ByteArrayOutputStream()
        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
        val imageBytes = out.toByteArray()
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }
}