package com.example.driverchecker.machinelearning.data

import android.graphics.Bitmap
import android.graphics.RectF
import com.example.driverchecker.machinelearning.pytorch.ClassifierTorchModel


// ---------------------------------- INPUT ----------------------------------

typealias ImageDetectionBaseInput = MachineLearningBaseInput<Bitmap>
typealias IImageDetectionData = IMachineLearningData<Bitmap>

// ---------------------------------- OUTPUT ----------------------------------

interface IImageDetectionBox {
    var classIndex: Int
    var rect: RectF
}

typealias ImageDetectionBaseOutput<Superclass> = ClassificationBaseOutput<IImageDetectionBox, Superclass>
typealias ImageDetectionOutput<Superclass> = ClassificationOutput<IImageDetectionData, IImageDetectionBox, Superclass>

data class ImageDetectionBox (override var classIndex: Int, override var rect: RectF) : IImageDetectionBox

// ---------------------------------- TYPE ALIASES ----------------------------------

typealias IImageDetectionResult<Superclass> = IClassificationResult<IImageDetectionBox, Superclass>
typealias IImageDetectionWithInput<Superclass> = IClassificationResultWithInput<IImageDetectionData, IImageDetectionBox, Superclass>


typealias ImageDetectionListOutput<Superclass> = ClassificationListOutput<IImageDetectionData, IImageDetectionBox, Superclass>
typealias ImageDetectionListBaseOutput<Superclass> = ClassificationListBaseOutput<IImageDetectionBox, Superclass>

typealias ImageDetectionArrayListOutput<Superclass> = ClassificationArrayListOutput<IImageDetectionData, IImageDetectionBox, Superclass>
typealias ImageDetectionArrayListBaseOutput<Superclass> = ClassificationArrayListBaseOutput<IImageDetectionBox, Superclass>


typealias ImageDetectionTorchModel<Superclass> = ClassifierTorchModel<IImageDetectionData, ImageDetectionArrayListOutput<Superclass>, Superclass>

typealias ImageDetectionRepository<Superclass> = ClassificationRepository<IImageDetectionData, ImageDetectionArrayListOutput<Superclass>, Superclass>