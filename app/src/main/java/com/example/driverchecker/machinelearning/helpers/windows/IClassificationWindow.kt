package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.IClassificationFinalResult
import com.example.driverchecker.machinelearning.data.WithConfAndClass
import com.example.driverchecker.machinelearning.data.WithConfAndGroups

interface IClassificationWindow<E : WithConfAndGroups<S>, S> : IMachineLearningWindow<E> {
    val supergroupCounter: Map<S, Int>

    override fun getFinalResults() : IClassificationFinalResult<S>
}
