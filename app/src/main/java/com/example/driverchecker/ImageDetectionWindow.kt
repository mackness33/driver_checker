package com.example.driverchecker

import com.example.driverchecker.machinelearning.data.IImageDetectionBox
import com.example.driverchecker.machinelearning.windows.MachineLearningWindow
import com.example.driverchecker.machinelearning_old.data.IImageDetectionData
import com.example.driverchecker.machinelearning_old.data.ImageDetectionArrayListOutput

class ImageDetectionWindow : MachineLearningWindow<IImageDetectionData, IImageDetectionBox, String, ImageDetectionArrayListOutput<String>>() {}