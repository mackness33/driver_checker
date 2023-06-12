package com.example.driverchecker

import com.example.driverchecker.machinelearning.data.ImageDetectionBox
import com.example.driverchecker.machinelearning.imagedetection.ImageDetectionArrayResult
import com.example.driverchecker.machinelearning.imagedetection.ImageDetectionResult

class ImageDetectionWindow : MLWindow<ImageDetectionBox, ImageDetectionArrayResult>() {}