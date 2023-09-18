package com.example.driverchecker.machinelearning.helpers.windows.factories

import com.example.driverchecker.machinelearning.data.IClassificationOutputStats
import com.example.driverchecker.machinelearning.helpers.windows.IClassificationWindow
import com.example.driverchecker.machinelearning.helpers.windows.IMachineLearningWindow

abstract class ClassificationWindowFactory<E : IClassificationOutputStats<S>, S> :
    WindowFactory<E>() {

    abstract fun buildClassificationWindow(frames: Int, threshold: Float, groups: Set<S>): IClassificationWindow<E, S>
}

interface IClassificationWindowFactory<E : IClassificationOutputStats<S>, S> :
    IWindowFactory<E> {

    fun buildClassificationWindow(frames: Int, threshold: Float, groups: Set<S>): IClassificationWindow<E, S>
}