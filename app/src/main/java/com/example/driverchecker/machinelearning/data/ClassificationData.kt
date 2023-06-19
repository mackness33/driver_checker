package com.example.driverchecker.machinelearning.data

import android.graphics.Bitmap
import android.graphics.RectF

// ---------------------------------- CLASSES ----------------------------------

interface IClassification {
    val name: String
    val index: Int
}

data class Classification (override val name: String, override val index: Int) : IClassification


// ---------------------------------- INPUT ----------------------------------

//interface IImageDetectionData :  IMachineLearningData<Bitmap>
typealias IImageDetectionData =  IMachineLearningData<Bitmap>

data class ImageDetectionBaseInput  (
    override val data: Bitmap
) : IImageDetectionData


// ---------------------------------- OUTPUT ----------------------------------

typealias IImageDetectionResult = IMachineLearningResult<ImageDetectionBox>

typealias IImageDetectionWithInput = IMachineLearningResultWithInput<Bitmap, ImageDetectionBox>

data class ImageDetectionBaseOutput(
    override val result: ImageDetectionBox,
    override val confidence: Float,
    override val classes: List<Int>
) : IImageDetectionResult

data class ImageDetectionOutput(
    override val result: ImageDetectionBox,
    override val confidence: Float,
    override val data: Bitmap,
    override val classes: List<Int>
) : IImageDetectionWithInput

data class ImageDetectionBox (var classIndex: Int, var rect: RectF)


// ---------------------------------- TYPE ALIASES ----------------------------------

typealias ImageDetectionResult = MLResult<ImageDetectionBox>
typealias ImageDetectionArrayResult = ArrayList<ImageDetectionResult>



// data class MachineLearningWindowOutput<Data, Result>(override val result: Result, override val confidence: Float, val classes: List<Int>, val data: Data) : IMachineLearningResult<Result>