package com.example.driverchecker.machinelearning.data

import android.graphics.Bitmap
import android.graphics.RectF
import com.example.driverchecker.machinelearning.models.pytorch.ClassifierTorchModel


// ---------------------------------- INPUT ----------------------------------
typealias ImageDetectionInput = MachineLearningInput<Bitmap>
typealias IImageDetectionInput = IMachineLearningInput<Bitmap>

// ---------------------------------- OUTPUT ----------------------------------

interface IImageDetectionItem<S> : IClassificationItem<S> {
    var classIndex: Int
    var rect: RectF
}

data class ImageDetectionItem<S> (
    override var classIndex: Int,
    override var rect: RectF,
    override val confidence: Float,
    override val classification: IClassification<S>
) : IImageDetectionItem<S>

typealias IImageDetectionOutput<S> = IClassificationOutput<IImageDetectionInput, IImageDetectionItem<S>, S>
typealias IImageDetectionFinalResult<S> = IClassificationFinalResult<S>

typealias ImageDetectionOutput<S> = ClassificationOutput<IImageDetectionInput, IImageDetectionItem<S>, S>
typealias ImageDetectionFinalResult<S> = ClassificationFinalResult<S>

// ---------------------------------- TYPE ALIASES ----------------------------------

typealias ImageDetectionTorchModel<S> = ClassifierTorchModel<IImageDetectionInput, IImageDetectionOutput<S>, S>

//typealias ImageDetectionRepository<S> = ClassificationRepository<IImageDetectionInput, IImageDetectionOutput<S>, S>