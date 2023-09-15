package com.example.driverchecker.machinelearning.data

import android.graphics.Bitmap
import android.graphics.RectF
import com.example.driverchecker.machinelearning.models.pytorch.ClassifierTorchModel


// ---------------------------------- INPUT ----------------------------------
typealias ImageDetectionInput = MachineLearningInput<Bitmap>
typealias IImageDetectionInput = IMachineLearningInput<Bitmap>

// ---------------------------------- BASIC OUTPUT ----------------------------------

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

typealias IImageDetectionOutput<S> = IClassificationOutput<IImageDetectionItem<S>, S>
typealias IImageDetectionFinalResult<S> = IClassificationFinalResult<S>

typealias ImageDetectionOutput<S> = ClassificationOutput<IImageDetectionItem<S>, S>
typealias ImageDetectionFinalResult<S> = ClassificationFinalResult<S>

// ---------------------------------- FULL OUTPUT ----------------------------------

interface IImageDetectionFullItem<S> : IClassificationFullItem<S>, IImageDetectionItem<S>

data class ImageDetectionFullItem<S> (
    override var classIndex: Int,
    override var rect: RectF,
    override val confidence: Float,
    override val classification: IClassification<S>
) : IImageDetectionFullItem<S>

typealias IImageDetectionFullOutput<S> = IClassificationFullOutput<IImageDetectionInput, IImageDetectionFullItem<S>, S>
typealias IImageDetectionFullFinalResult<S> = IClassificationFullFinalResult<S>

typealias ImageDetectionFullOutput<S> = ClassificationFullOutput<IImageDetectionInput, IImageDetectionFullItem<S>, S>
typealias ImageDetectionFullFinalResult<S> = ClassificationFullFinalResult<S>

// ---------------------------------- TYPE ALIASES ----------------------------------

typealias ImageDetectionTorchModel<S> = ClassifierTorchModel<IImageDetectionInput, IImageDetectionFullOutput<S>, S>

//typealias ImageDetectionRepository<S> = ClassificationRepository<IImageDetectionInput, IImageDetectionOutput<S>, S>