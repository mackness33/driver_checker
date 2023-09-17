package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.IClassificationOutputStats
import com.example.driverchecker.machinelearning.data.IMachineLearningOutputStats

abstract class ClassificationWindowFactory<E : IClassificationOutputStats<S>, S> :
    WindowFactory<E>() {

    abstract override fun buildWindow(): IClassificationWindow<E, S>
}