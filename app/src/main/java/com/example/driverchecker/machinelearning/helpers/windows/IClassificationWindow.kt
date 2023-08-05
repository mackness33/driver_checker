package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.WithConfAndGroups
import com.example.driverchecker.machinelearning.data.WithConfAndSuper

interface IClassificationWindow<E : WithConfAndGroups<S>, S> : IMachineLearningWindow<E> {
    val supergroupCounter: Map<S, Int>

    override fun getFinalResults() : WithConfAndSuper<S>
}
