package com.example.driverchecker

import com.example.driverchecker.machinelearning.data.IImageDetectionBox
import com.example.driverchecker.machinelearning.data.IImageDetectionData
import com.example.driverchecker.machinelearning.data.ImageDetectionArrayListOutput
import com.example.driverchecker.machinelearning.data.ImageDetectionBox

class ImageDetectionWindow : MLWindow<IImageDetectionData, IImageDetectionBox, String, ImageDetectionArrayListOutput<String>>() {}