package com.example.driverchecker.machinelearning.data

import android.graphics.Bitmap
import android.graphics.RectF
import com.example.driverchecker.machinelearning.models.pytorch.ClassifierTorchModel

// ---------------------------------- BASIC OUTPUT ----------------------------------

// ---------------------------------- TYPE ALIASES ----------------------------------

typealias ImageDetectionTorchModel<S> = ClassifierTorchModel<IImageDetectionInput, IImageDetectionFullOutput<S>, S>

//typealias ImageDetectionRepository<S> = ClassificationRepository<IImageDetectionInput, IImageDetectionOutput<S>, S>