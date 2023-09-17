package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.IMachineLearningOutputStats

abstract class WindowFactory<E : IMachineLearningOutputStats> {

    abstract fun buildWindow(): IMachineLearningWindow<E>
}