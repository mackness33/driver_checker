package com.example.driverchecker.machinelearning.data

import android.graphics.RectF

// ---------------------------------- MACHINE LEARNING ----------------------------------
typealias IMachineLearningItem = WithConfidence

data class MachineLearningItem (
    override val confidence: Float
) : IMachineLearningItem

typealias IMachineLearningFullItem = IMachineLearningItem

typealias MachineLearningFullItem = IMachineLearningFullItem



// ---------------------------------- CLASSIFICATION ----------------------------------
interface IClassificationItem<S> : IMachineLearningItem, WithClassification<S> {
    override val classification: IClassification<S>
}

data class ClassificationItem<S> (
    override val confidence: Float,
    override val classification: IClassification<S>,
) : IClassificationFullItem<S> {
    constructor(baseResult: IClassificationItem<S>) : this(
        baseResult.confidence, baseResult.classification
    )
}

interface IClassificationFullItem<S> : IMachineLearningFullItem, IClassificationItem<S>


data class ClassificationFullItem<S> (
    override val confidence: Float,
    override val classification: IClassification<S>,
) : IClassificationFullItem<S> {
    constructor(baseResult: IClassificationItem<S>) : this(
        baseResult.confidence, baseResult.classification
    )
}



// ---------------------------------- IMAGE DETECTION ----------------------------------
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


interface IImageDetectionFullItem<S> : IClassificationFullItem<S>, IImageDetectionItem<S>

data class ImageDetectionFullItem<S> (
    override var classIndex: Int,
    override var rect: RectF,
    override val confidence: Float,
    override val classification: IClassification<S>
) : IImageDetectionFullItem<S>