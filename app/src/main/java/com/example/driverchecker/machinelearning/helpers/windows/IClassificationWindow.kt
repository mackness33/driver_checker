package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.IClassificationOutput
import com.example.driverchecker.machinelearning.data.IClassificationFinalResult

interface IClassificationWindow<E : IClassificationOutput<S>, S> : IMachineLearningWindow<E> {
    val supergroupCounter: Map<S, Int>

    override fun getFinalResults() : IClassificationFinalResult<S>
}
