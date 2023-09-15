package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.IClassificationFinalResult
import com.example.driverchecker.machinelearning.data.IClassificationOutputStats

interface IClassificationWindow<E : IClassificationOutputStats<S>, S> : IMachineLearningWindow<E> {
    val supergroupCounter: Map<S, Int>

    override fun getFinalResults() : IClassificationFinalResult<S>
}
