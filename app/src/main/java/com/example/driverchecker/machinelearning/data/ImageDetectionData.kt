package com.example.driverchecker.machinelearning.data

import android.graphics.Bitmap
import android.graphics.RectF


// ---------------------------------- INPUT ----------------------------------

data class ImageDetectionBaseInput  (
    override val data: Bitmap
) : IImageDetectionData


// ---------------------------------- OUTPUT ----------------------------------

interface IImageDetectionBox {
    var classIndex: Int
    var rect: RectF
}

data class ImageDetectionBaseOutput<Superclass>(
    override val result: IImageDetectionBox,
    override val confidence: Float,
    override val group: IClassification<Superclass>
) : IImageDetectionResult<Superclass>

data class ImageDetectionOutput<Superclass>(
    override val result: IImageDetectionBox,
    override val confidence: Float,
    override val data: IImageDetectionData,
    override val group: IClassification<Superclass>
) : IImageDetectionWithInput<Superclass>

data class ImageDetectionBox (override var classIndex: Int, override var rect: RectF) : IImageDetectionBox


// ---------------------------------- TYPE ALIASES ----------------------------------

typealias IImageDetectionData =  IMachineLearningData<Bitmap>


typealias IImageDetectionResult<Superclass> = IMachineLearningResult<IImageDetectionBox, Superclass>
typealias IImageDetectionWithInput<Superclass> = IMachineLearningResultWithInput<IImageDetectionData, IImageDetectionBox, Superclass>


typealias ImageDetectionArrayOutput<Superclass> = MachineLearningArrayOutput<IImageDetectionData, IImageDetectionBox, Superclass>
typealias ImageDetectionArrayBaseOutput<Superclass> = MachineLearningArrayBaseOutput<ImageDetectionBox, Superclass>

typealias ImageDetectionListOutput<Superclass> = MachineLearningListOutput<IImageDetectionData, IImageDetectionBox, Superclass>
typealias ImageDetectionListBaseOutput<Superclass> = MachineLearningListBaseOutput<IImageDetectionBox, Superclass>

typealias ImageDetectionArrayListOutput<Superclass> = MachineLearningArrayListOutput<IImageDetectionData, IImageDetectionBox, Superclass>
typealias ImageDetectionArrayListBaseOutput<Superclass> = MachineLearningArrayListBaseOutput<IImageDetectionBox, Superclass>



// data class MachineLearningWindowOutput<Data, Result>(override val result: Result, override val confidence: Float, val classes: List<Int>, val data: Data) : IMachineLearningResult<Result>