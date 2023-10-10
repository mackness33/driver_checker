package com.example.driverchecker.machinelearning.data

import android.graphics.Bitmap
import android.graphics.RectF
import com.example.driverchecker.machinelearning.models.pytorch.ClassifierTorchModel

// ---------------------------------- BASIC OUTPUT ----------------------------------


typealias IOldImageDetectionFinalResult<S> = IOldClassificationFinalResult<S>
typealias IImageDetectionFinalResult<S> = IClassificationFinalResult<S>

typealias ImageDetectionFinalResultOld<S> = ClassificationFinalResultOld<S>
typealias ImageDetectionFinalResult<S> = ClassificationFinalResult<S>

// ---------------------------------- FULL OUTPUT ----------------------------------


typealias IOldImageDetectionFullFinalResult<S> = IOldClassificationFullFinalResult<S>

typealias ImageDetectionFullFinalResultOld<S> = ClassificationFullFinalResultOld<S>

// ---------------------------------- TYPE ALIASES ----------------------------------

typealias ImageDetectionTorchModel<S> = ClassifierTorchModel<IImageDetectionInput, IImageDetectionFullOutput<S>, S>

//typealias ImageDetectionRepository<S> = ClassificationRepository<IImageDetectionInput, IImageDetectionOutput<S>, S>