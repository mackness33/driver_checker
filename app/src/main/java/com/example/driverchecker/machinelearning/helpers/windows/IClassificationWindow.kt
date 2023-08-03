package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.IClassificationFinalResult
import com.example.driverchecker.machinelearning.data.WithConfAndClas
import com.example.driverchecker.machinelearning.data.WithConfAndGroup

interface IClassificationWindow<E : WithConfAndClas<S>, S> : IMachineLearningWindow<E> {
    val supergroupCounter: Map<S, Int>

    override fun getFinalResults() : IClassificationFinalResult<S>
}
