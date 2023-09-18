package com.example.driverchecker.machinelearning.helpers.windows.factories

import com.example.driverchecker.machinelearning.data.IMachineLearningOutputStats
import com.example.driverchecker.machinelearning.helpers.windows.IMachineLearningWindow

abstract class WindowFactory<E : IMachineLearningOutputStats> {

    abstract fun buildMachineLearningWindow(frames: Int, threshold: Float): IMachineLearningWindow<E>
}