package com.example.driverchecker.machinelearning.data

import android.graphics.Bitmap

// ---------------------------------- DEFINITION ----------------------------------
interface WithInput<I> {
    val input: I
}


// ---------------------------------- MACHINE LEARNING ----------------------------------
typealias IMachineLearningInput<I> = WithInput<I>

data class MachineLearningInput<I>(
    override val input: I,
) : IMachineLearningInput<I>



// ---------------------------------- IMAGE DETECTION  ----------------------------------
data class ImageDetectionInput(
    override val input: Bitmap,
    override val preProcessedImage: Bitmap?,
    override val modelRatio: Pair<Int, Int>? = null,
    override val imageRatio: Pair<Float, Float>? = null,
) : IImageDetectionInput

interface IImageDetectionInput : IMachineLearningInput<Bitmap> {
    val preProcessedImage: Bitmap?
    val modelRatio: Pair<Int, Int>?
    val imageRatio: Pair<Float, Float>?
}