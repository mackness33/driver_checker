package com.example.driverchecker.machinelearning.helpers.listeners

import com.example.driverchecker.machinelearning.data.*

interface ClassificationListener<D, R : WithConfAndGroups<S>, S> : MachineLearningListener<D, R> {
    override fun collectLiveEvaluations (state: LiveEvaluationStateInterface) {
        when (state) {
            is LiveClassificationState.Start -> onLiveClassificationStart(state)
            is LiveClassificationState.End<*> -> onLiveClassificationEnd(state as LiveClassificationState.End<S>)
            else -> super.collectLiveEvaluations(state)
        }
    }

    // handler of mlRepo in start (as a classification)
    fun onLiveClassificationStart (state: LiveClassificationState.Start)

    fun onLiveClassificationEnd (state: LiveClassificationState.End<S>)
}