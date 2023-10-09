package com.example.driverchecker.machinelearning.helpers.windows.factories

import com.example.driverchecker.machinelearning.data.IClassificationOutputStats
import com.example.driverchecker.machinelearning.helpers.windows.singles.IClassificationWindowOld

abstract class ClassificationWindowFactory<E : IClassificationOutputStats<S>, S> :
    WindowFactory<E>() {

    abstract fun buildClassificationWindow(frames: Int, threshold: Float, groups: Set<S>): IClassificationWindowOld<E, S>
}

interface IClassificationWindowFactory<E : IClassificationOutputStats<S>, S> :
    IMachineLearningWindowFactory<E> {

    fun buildClassificationWindow(frames: Int, threshold: Float, groups: Set<S>): IClassificationWindowOld<E, S>
}