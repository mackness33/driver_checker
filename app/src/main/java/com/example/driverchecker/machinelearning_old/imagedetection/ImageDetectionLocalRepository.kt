package com.example.driverchecker.machinelearning_old.imagedetection

import com.example.driverchecker.machinelearning.data.IImageDetectionBox
import com.example.driverchecker.machinelearning_old.data.*
import com.example.driverchecker.machinelearning_old.general.local.MLLocalModel
import com.example.driverchecker.machinelearning_old.general.local.MLLocalRepository

class ImageDetectionLocalRepository (override val model: MLLocalModel<IImageDetectionData, ImageDetectionArrayListOutput<String>>?) : MLLocalRepository<IImageDetectionData, IImageDetectionBox, String, ImageDetectionArrayListOutput<String>> (model) {}