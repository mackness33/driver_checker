package com.example.driverchecker.machinelearning_old.data

import android.graphics.Bitmap
import android.graphics.RectF
import com.example.driverchecker.machinelearning.data.IClassification
import com.example.driverchecker.machinelearning.data.IImageDetectionBox
import com.example.driverchecker.machinelearning.data.IMachineLearningData
import com.example.driverchecker.machinelearning.data.ImageDetectionBox


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

data class ImageDetectionBox (override var classIndex: Int, override var rect: RectF) :
    IImageDetectionBox


// ---------------------------------- TYPE ALIASES ----------------------------------

typealias IImageDetectionData = IMachineLearningData<Bitmap>


typealias IImageDetectionResult<Superclass> = IMachineLearningResultOld<IImageDetectionBox, Superclass>
typealias IImageDetectionWithInput<Superclass> = IMachineLearningResultWithInputOld<IImageDetectionData, IImageDetectionBox, Superclass>


typealias ImageDetectionArrayOutput<Superclass> = MachineLearningArrayOutputOld<IImageDetectionData, IImageDetectionBox, Superclass>
typealias ImageDetectionArrayBaseOutput<Superclass> = MachineLearningArrayBaseOutputOld<ImageDetectionBox, Superclass>

typealias ImageDetectionListOutput<Superclass> = MachineLearningListOutputOld<IImageDetectionData, IImageDetectionBox, Superclass>
typealias ImageDetectionListBaseOutput<Superclass> = MachineLearningListBaseOutputOld<IImageDetectionBox, Superclass>

typealias ImageDetectionArrayListOutput<Superclass> = MachineLearningArrayListOutputOld<IImageDetectionData, IImageDetectionBox, Superclass>
typealias ImageDetectionArrayListBaseOutput<Superclass> = MachineLearningArrayListBaseOutputOld<IImageDetectionBox, Superclass>



// data class MachineLearningWindowOutput<Data, Result>(override val result: Result, override val confidence: Float, val classes: List<Int>, val data: Data) : IMachineLearningResult<Result>