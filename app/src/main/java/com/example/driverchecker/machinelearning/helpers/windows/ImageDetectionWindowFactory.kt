package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.IClassificationOutputStats
import com.example.driverchecker.machinelearning.data.IImageDetectionOutputStats
import com.example.driverchecker.machinelearning.helpers.windows.factories.ClassificationWindowFactory
import com.example.driverchecker.machinelearning.helpers.windows.factories.IClassificationWindowFactory

//abstract class ImageDetectionWindowFactory1 :
//    ClassificationWindowFactory<IClassificationOutputStats<String>, String>() {
//
//    abstract override fun buildWindow(): IClassificationWindow<IClassificationOutputStats<String>, String>
//}

typealias ImageDetectionWindowFactory = ClassificationWindowFactory<IImageDetectionOutputStats<String>, String>
typealias IImageDetectionWindowFactory = IClassificationWindowFactory<IImageDetectionOutputStats<String>, String>
typealias ImageDetectionWindow = ClassificationWindow<IImageDetectionOutputStats<String>, String>
typealias IImageDetectionWindow = IClassificationWindow<IImageDetectionOutputStats<String>, String>
typealias AImageDetectionWindow = AClassificationWindow<IImageDetectionOutputStats<String>, String>