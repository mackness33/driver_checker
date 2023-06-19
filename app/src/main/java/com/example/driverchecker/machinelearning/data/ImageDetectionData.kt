package com.example.driverchecker.machinelearning.data

import android.graphics.Bitmap
import android.graphics.RectF


// ---------------------------------- INPUT ----------------------------------

//interface IImageDetectionData :  IMachineLearningData<Bitmap>
typealias IImageDetectionData =  IMachineLearningData<Bitmap>

data class ImageDetectionBaseInput  (
    override val data: Bitmap
) : IImageDetectionData


// ---------------------------------- OUTPUT ----------------------------------

typealias IImageDetectionResult = IMachineLearningResult<IImageDetectionBox>

typealias IImageDetectionWithInput = IMachineLearningResultWithInput<IImageDetectionData, IImageDetectionBox>

interface IImageDetectionBox {
    var classIndex: Int
    var rect: RectF
}

data class ImageDetectionBaseOutput(
    override val result: IImageDetectionBox,
    override val confidence: Float,
    override val classes: List<Int>
) : IImageDetectionResult

data class ImageDetectionOutput(
    override val result: IImageDetectionBox,
    override val confidence: Float,
    override val data: IImageDetectionData,
    override val classes: List<Int>
) : IImageDetectionWithInput

data class ImageDetectionBox (override var classIndex: Int, override var rect: RectF) : IImageDetectionBox


// ---------------------------------- TYPE ALIASES ----------------------------------

typealias ImageDetectionArrayOutput = MachineLearningArrayOutput<IImageDetectionData, IImageDetectionBox>
typealias ImageDetectionArrayBaseOutput = MachineLearningArrayBaseOutput<ImageDetectionBox>

typealias ImageDetectionListOutput = MachineLearningListOutput<IImageDetectionData, IImageDetectionBox>
typealias ImageDetectionListBaseOutput = MachineLearningListBaseOutput<IImageDetectionBox>


typealias ImageDetectionArrayListOutput = MachineLearningArrayListOutput<IImageDetectionData, IImageDetectionBox>
typealias ImageDetectionArrayListBaseOutput = MachineLearningArrayListBaseOutput<IImageDetectionBox>



// data class MachineLearningWindowOutput<Data, Result>(override val result: Result, override val confidence: Float, val classes: List<Int>, val data: Data) : IMachineLearningResult<Result>