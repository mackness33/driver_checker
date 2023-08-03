package com.example.driverchecker.machinelearning.data

import android.graphics.Bitmap
import android.graphics.RectF
import com.example.driverchecker.machinelearning.models.pytorch.ClassifierTorchModel


// ---------------------------------- INPUT ----------------------------------

typealias ImageDetectionBaseInput = MachineLearningInput<Bitmap>
typealias IImageDetectionData = IMachineLearningInput<Bitmap>

// ---------------------------------- OUTPUT ----------------------------------

interface IImageDetectionItem<S> : WithConfAndSupergroup<S> {
    var classIndex: Int
    var rect: RectF
}

typealias IImageDetectionResult<S> = IClassificationResult<IImageDetectionData, IImageDetectionItem<S>, S>

typealias ImageDetectionResult<S> = ClassificationResult<IImageDetectionData, IImageDetectionItem<S>, S>
typealias ImageDetectionOutput<S> = ClassificationOutput<IImageDetectionData, IImageDetectionItem<S>, S>

data class ImageDetectionItem<S> (
    override var classIndex: Int,
    override var rect: RectF,
    override val confidence: Float,
    override val group: IClassification<S>
) : IImageDetectionItem<S>

// ---------------------------------- TYPE ALIASES ----------------------------------

typealias ImageDetectionTorchModel<S> = ClassifierTorchModel<IImageDetectionData, IImageDetectionResult<S>, S>

typealias ImageDetectionRepository<S> = ClassificationRepository<IImageDetectionData, IImageDetectionResult<S>, S>