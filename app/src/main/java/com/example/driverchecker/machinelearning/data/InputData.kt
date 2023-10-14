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
    override var imageRatio: Pair<Float, Float> = Pair(1.0f, 1.0f),
) : IImageDetectionInput {
    constructor (original: IImageDetectionInput) : this(
        original.input, original.index, original.imageRatio
    )

    override fun resizeImage (width: Int, height: Int) {
        imageRatio = input.width.toFloat()/width to input.height.toFloat()/height
        input = Bitmap.createScaledBitmap(this.input, width, height, true)
    }

    override fun rotate (angle: Float) {
        input = BitmapUtils.rotateBitmap(input, angle)
    }

    override fun copy () : ImageDetectionInput {
        return ImageDetectionInput(this)
    }
}

interface IImageDetectionInput : IMachineLearningInput<Bitmap> {
    val imageRatio: Pair<Float, Float>
    fun resizeImage (width: Int, height: Int)
    fun rotate (angle: Float)
    fun copy () : IImageDetectionInput
}