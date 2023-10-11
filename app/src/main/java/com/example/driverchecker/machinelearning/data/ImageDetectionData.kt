package com.example.driverchecker.machinelearning.data

import com.example.driverchecker.machinelearning.models.pytorch.ClassifierTorchModel

// ---------------------------------- BASIC OUTPUT ----------------------------------

// ---------------------------------- TYPE ALIASES ----------------------------------

typealias ImageDetectionTorchModel<S> = ClassifierTorchModel<IImageDetectionInputOld, IImageDetectionFullOutput<S>, S>

//typealias ImageDetectionRepository<S> = ClassificationRepository<IImageDetectionInput, IImageDetectionOutput<S>, S>