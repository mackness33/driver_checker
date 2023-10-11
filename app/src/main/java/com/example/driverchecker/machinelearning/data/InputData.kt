package com.example.driverchecker.machinelearning.data

import android.graphics.Bitmap
import com.example.driverchecker.utils.BitmapUtils

// ---------------------------------- MACHINE LEARNING ----------------------------------
interface IMachineLearningInput<I> : WithInput<I>, WithIndex

data class MachineLearningInput<I>(
    override val input: I,
    override val index: Int
) : IMachineLearningInput<I>



// ---------------------------------- IMAGE DETECTION  ----------------------------------
data class ImageDetectionInput(
    override var input: Bitmap,
    override val index: Int,
    override var imageRatio: Pair<Float, Float>? = null,
) : IImageDetectionInput {
    override fun resizeImage (width: Int, height: Int) {
        input = Bitmap.createScaledBitmap(this.input, width, height, true)
        imageRatio = input.width.toFloat()/width to input.height.toFloat()/height
    }

    override fun rotate (angle: Float) {
        input = BitmapUtils.rotateBitmap(input, -90f)
    }
}

interface IImageDetectionInput : IMachineLearningInput<Bitmap> {
    val imageRatio: Pair<Float, Float>?
    fun resizeImage (width: Int, height: Int)
    fun rotate (angle: Float)
}



// ---------------------------------- MACHINE LEARNING OLD ----------------------------------
typealias IMachineLearningInputOld<I> = WithInput<I>

data class MachineLearningInputOld<I>(
    override val input: I,
) : IMachineLearningInputOld<I>



// ---------------------------------- IMAGE DETECTION OLD  ----------------------------------
data class ImageDetectionInputOld(
    override val input: Bitmap,
    override val preProcessedImage: Bitmap?,
    override val modelRatio: Pair<Int, Int>? = null,
    override val imageRatio: Pair<Float, Float>? = null,
) : IImageDetectionInputOld

interface IImageDetectionInputOld : IMachineLearningInputOld<Bitmap> {
    val preProcessedImage: Bitmap?
    val modelRatio: Pair<Int, Int>?
    val imageRatio: Pair<Float, Float>?
}