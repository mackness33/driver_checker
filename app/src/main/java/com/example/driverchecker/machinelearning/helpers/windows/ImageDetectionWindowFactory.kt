package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.IClassificationOutputStats
import com.example.driverchecker.machinelearning.data.IMachineLearningOutputStats

//abstract class ImageDetectionWindowFactory1 :
//    ClassificationWindowFactory<IClassificationOutputStats<String>, String>() {
//
//    abstract override fun buildWindow(): IClassificationWindow<IClassificationOutputStats<String>, String>
//}

typealias ImageDetectionWindowFactory = ClassificationWindowFactory<IClassificationOutputStats<String>, String>
typealias ImageDetectionWindow = ClassificationWindow<IClassificationOutputStats<String>, String>
typealias IImageDetectionWindow = IClassificationWindow<IClassificationOutputStats<String>, String>
typealias AImageDetectionWindow = AClassificationWindow<IClassificationOutputStats<String>, String>