package com.example.driverchecker.machinelearning.data

import android.graphics.Bitmap
import android.graphics.RectF
import com.example.driverchecker.machinelearning_old.data.*
import com.example.driverchecker.machinelearning_old.data.IImageDetectionBox
import com.example.driverchecker.machinelearning_old.data.ImageDetectionBox


// ---------------------------------- INPUT ----------------------------------

typealias ImageDetectionBaseInput = MachineLearningBaseInput<Bitmap>
typealias IImageDetectionData = IMachineLearningData<Bitmap>

// ---------------------------------- OUTPUT ----------------------------------

interface IImageDetectionBox {
    var classIndex: Int
    var rect: RectF
}

typealias ImageDetectionBaseOutput<Superclass> = IClassificationResult<IImageDetectionBox, Superclass>
typealias ImageDetectionOutput<Superclass> = IClassificationResultWithInput<IImageDetectionData, IImageDetectionBox, Superclass>

data class ImageDetectionBox (override var classIndex: Int, override var rect: RectF) : IImageDetectionBox

// ---------------------------------- TYPE ALIASES ----------------------------------

typealias IImageDetectionResult<Superclass> = IClassificationResult<IImageDetectionBox, Superclass>
typealias IImageDetectionWithInput<Superclass> = IClassificationResultWithInput<IImageDetectionData, IImageDetectionBox, Superclass>


typealias ImageDetectionArrayOutput<Superclass> = ClassificationArrayOutput<IImageDetectionData, IImageDetectionBox, Superclass>
typealias ImageDetectionArrayBaseOutput<Superclass> = ClassificationArrayBaseOutput<ImageDetectionBox, Superclass>

typealias ImageDetectionListOutput<Superclass> = ClassificationListOutput<IImageDetectionData, IImageDetectionBox, Superclass>
typealias ImageDetectionListBaseOutput<Superclass> = ClassificationListBaseOutput<IImageDetectionBox, Superclass>

typealias ImageDetectionArrayListOutput<Superclass> = ClassificationArrayListOutput<IImageDetectionData, IImageDetectionBox, Superclass>
typealias ImageDetectionArrayListBaseOutput<Superclass> = ClassificationArrayListBaseOutput<IImageDetectionBox, Superclass>