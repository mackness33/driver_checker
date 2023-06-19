package com.example.driverchecker.machinelearning.imagedetection

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.general.local.MLLocalModel
import com.example.driverchecker.machinelearning.general.local.MLLocalRepository

class ImageDetectionLocalRepository (override val model: MLLocalModel<IImageDetectionData, ImageDetectionArrayListOutput>?) : MLLocalRepository<IImageDetectionData, IImageDetectionBox, ImageDetectionArrayListOutput> (model) {}