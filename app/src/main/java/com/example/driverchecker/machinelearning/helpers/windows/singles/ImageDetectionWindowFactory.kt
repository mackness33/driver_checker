package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.IImageDetectionOutputStats
import com.example.driverchecker.machinelearning.helpers.windows.factories.ClassificationWindowFactory
import com.example.driverchecker.machinelearning.helpers.windows.factories.IClassificationWindowFactory
import com.example.driverchecker.machinelearning.helpers.windows.singles.AClassificationWindowOld
import com.example.driverchecker.machinelearning.helpers.windows.singles.ClassificationWindowOld
import com.example.driverchecker.machinelearning.helpers.windows.singles.IClassificationWindow

//abstract class ImageDetectionWindowFactory1 :
//    ClassificationWindowFactory<IClassificationOutputStats<String>, String>() {
//
//    abstract override fun buildWindow(): IClassificationWindow<IClassificationOutputStats<String>, String>
//}

typealias ImageDetectionWindowFactory = ClassificationWindowFactory<IImageDetectionOutputStats<String>, String>
typealias IImageDetectionWindowFactory = IClassificationWindowFactory<IImageDetectionOutputStats<String>, String>
typealias ImageDetectionWindow = ClassificationWindowOld<IImageDetectionOutputStats<String>, String>
typealias IImageDetectionWindow = IClassificationWindow<IImageDetectionOutputStats<String>, String>
typealias AImageDetectionWindow = AClassificationWindowOld<IImageDetectionOutputStats<String>, String>