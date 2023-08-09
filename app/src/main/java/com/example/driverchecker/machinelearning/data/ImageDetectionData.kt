package com.example.driverchecker.machinelearning.data

import android.graphics.Bitmap
import android.graphics.RectF
import com.example.driverchecker.machinelearning.models.pytorch.ClassifierTorchModel


// ---------------------------------- INPUT ----------------------------------

typealias ImageDetectionBaseInput = MachineLearningInput<Bitmap>
typealias IImageDetectionData = IMachineLearningData<Bitmap>

// ---------------------------------- OUTPUT ----------------------------------

interface IImageDetectionItem<S> : IClassificationItem<S> {
    var classIndex: Int
    var rect: RectF
}

typealias IImageDetectionOutput<S> = IClassificationOutput<IImageDetectionData, IImageDetectionItem<S>, S>
typealias IImageDetectionFinalResult<S> = IClassificationFinalResult<S>

typealias ImageDetectionOutput<S> = ClassificationOutput<IImageDetectionData, IImageDetectionItem<S>, S>
typealias ImageDetectionFinalResult<S> = ClassificationFinalResult<S>

// ---------------------------------- TYPE ALIASES ----------------------------------

typealias ImageDetectionTorchModel<S> = ClassifierTorchModel<IImageDetectionData, IImageDetectionResultOld<S>, S>

//typealias ImageDetectionRepository<S> = ClassificationRepository<IImageDetectionData, IImageDetectionResult<S>, S>

// ---------------------------------- OLD OUTPUT ----------------------------------

interface IImageDetectionItemOld<S> : WithConfAndClass<S> {
    var classIndex: Int
    var rect: RectF
}

typealias IImageDetectionResultOld<S> = IClassificationResultOld<IImageDetectionData, IImageDetectionItemOld<S>, S>
typealias IImageDetectionOutputOld<S> = IClassificationOutputOld<IImageDetectionData, IImageDetectionResultOld<S>, S>

typealias ImageDetectionResultOld<S> = ClassificationResultOld<IImageDetectionData, IImageDetectionItemOld<S>, S>
typealias ImageDetectionOutputOld<S> = ClassificationOutputOld<IImageDetectionData, IImageDetectionResultOld<S>, S>

data class ImageDetectionItemOld<S> (
    override var classIndex: Int,
    override var rect: RectF,
    override val confidence: Float,
    override val classification: IClassification<S>
) : IImageDetectionItemOld<S>