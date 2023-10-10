package com.example.driverchecker.machinelearning.data

import android.graphics.Bitmap
import android.graphics.RectF
import com.example.driverchecker.machinelearning.models.pytorch.ClassifierTorchModel

// ---------------------------------- BASIC OUTPUT ----------------------------------


typealias IImageDetectionOutputStats<S> = IClassificationOutputStats<S>
typealias IImageDetectionOutput<S> = IClassificationOutput<IImageDetectionItem<S>, S>
typealias IOldImageDetectionFinalResult<S> = IOldClassificationFinalResult<S>
typealias IImageDetectionFinalResult<S> = IClassificationFinalResult<S>

typealias ImageDetectionOutput<S> = ClassificationOutput<IImageDetectionItem<S>, S>
typealias ImageDetectionFinalResultOld<S> = ClassificationFinalResultOld<S>
typealias ImageDetectionFinalResult<S> = ClassificationFinalResult<S>

// ---------------------------------- FULL OUTPUT ----------------------------------


typealias IImageDetectionFullOutput<S> = IClassificationFullOutput<IImageDetectionInput, IImageDetectionFullItem<S>, S>
typealias IOldImageDetectionFullFinalResult<S> = IOldClassificationFullFinalResult<S>

typealias ImageDetectionFullOutput<S> = ClassificationFullOutput<IImageDetectionInput, IImageDetectionFullItem<S>, S>
typealias ImageDetectionFullFinalResultOld<S> = ClassificationFullFinalResultOld<S>

// ---------------------------------- TYPE ALIASES ----------------------------------

typealias ImageDetectionTorchModel<S> = ClassifierTorchModel<IImageDetectionInput, IImageDetectionFullOutput<S>, S>

//typealias ImageDetectionRepository<S> = ClassificationRepository<IImageDetectionInput, IImageDetectionOutput<S>, S>