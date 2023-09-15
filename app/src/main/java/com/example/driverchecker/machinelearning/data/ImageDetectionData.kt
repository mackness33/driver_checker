package com.example.driverchecker.machinelearning.data

import android.graphics.Bitmap
import android.graphics.RectF
import com.example.driverchecker.machinelearning.models.pytorch.ClassifierTorchModel


// ---------------------------------- INPUT ----------------------------------
typealias ImageDetectionInput = MachineLearningInput<Bitmap>
typealias IImageDetectionInput = IMachineLearningInput<Bitmap>

// ---------------------------------- OUTPUT ----------------------------------

interface IImageDetectionFullItem<S> : IClassificationFullItem<S> {
    var classIndex: Int
    var rect: RectF
}

data class ImageDetectionFullItem<S> (
    override var classIndex: Int,
    override var rect: RectF,
    override val confidence: Float,
    override val classification: IClassification<S>
) : IImageDetectionFullItem<S>

typealias IImageDetectionOutput<S> = IClassificationFullOutput<IImageDetectionInput, IImageDetectionFullItem<S>, S>
typealias IImageDetectionFinalResult<S> = IClassificationFullFinalResult<S>

typealias ImageDetectionOutput<S> = ClassificationFullOutput<IImageDetectionInput, IImageDetectionFullItem<S>, S>
typealias ImageDetectionFinalResult<S> = ClassificationFullFinalResult<S>

// ---------------------------------- TYPE ALIASES ----------------------------------

typealias ImageDetectionTorchModel<S> = ClassifierTorchModel<IImageDetectionInput, IImageDetectionOutput<S>, S>

//typealias ImageDetectionRepository<S> = ClassificationRepository<IImageDetectionInput, IImageDetectionOutput<S>, S>