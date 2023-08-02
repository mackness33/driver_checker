package com.example.driverchecker.machinelearning.data

import android.graphics.Bitmap
import android.graphics.RectF
import com.example.driverchecker.machinelearning.models.pytorch.ClassifierTorchModel


// ---------------------------------- INPUT ----------------------------------

typealias ImageDetectionBaseInput = MachineLearningInput<Bitmap>
typealias IImageDetectionData = IMachineLearningInput<Bitmap>

// ---------------------------------- OUTPUT ----------------------------------

interface IImageDetectionBox : WithConfidence{
    var classIndex: Int
    var rect: RectF
}

typealias ImageDetectionResult<Superclass> = ClassificationResult<IImageDetectionData, IImageDetectionBox, Superclass>
typealias ImageDetectionOutput<Superclass> = ClassificationOutput<IImageDetectionData, IImageDetectionBox, Superclass>

data class ImageDetectionBox (
    override var classIndex: Int,
    override var rect: RectF,
    override val confidence: Float
) : IImageDetectionBox

// ---------------------------------- TYPE ALIASES ----------------------------------

typealias IImageDetectionResult<Superclass> = IClassificationBasicItem<IImageDetectionBox, Superclass>
typealias IImageDetectionWithInput<Superclass> = IClassificationBasicItemWithInput<IImageDetectionData, IImageDetectionBox, Superclass>


typealias ImageDetectionListOutput<Superclass> = ClassificationListOutput<IImageDetectionData, IImageDetectionBox, Superclass>
typealias ImageDetectionListBaseOutput<Superclass> = ClassificationListBaseOutput<IImageDetectionBox, Superclass>

typealias ImageDetectionArrayListOutput<Superclass> = ClassificationArrayListOutput<IImageDetectionData, IImageDetectionBox, Superclass>
typealias ImageDetectionArrayListBaseOutput<Superclass> = ClassificationArrayListBaseOutput<IImageDetectionBox, Superclass>


typealias ImageDetectionTorchModel<Superclass> = ClassifierTorchModel<IImageDetectionData, ImageDetectionArrayListOutput<Superclass>, Superclass>

typealias ImageDetectionRepository<Superclass> = ClassificationRepository<IImageDetectionData, ImageDetectionArrayListOutput<Superclass>, Superclass>