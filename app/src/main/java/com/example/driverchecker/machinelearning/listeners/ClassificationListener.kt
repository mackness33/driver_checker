package com.example.driverchecker.machinelearning.listeners

import com.example.driverchecker.machinelearning.data.LiveClassificationState
import com.example.driverchecker.machinelearning.data.LiveEvaluationStateInterface
import com.example.driverchecker.machinelearning.data.WithConfidence

interface ClassificationListener<D, R : WithConfidence> : MachineLearningListener<D, R> {
    override fun collectLiveEvaluations (state: LiveEvaluationStateInterface<R>) {
        when (state) {
            is LiveClassificationState.Start -> onLiveClassificationStart(state)
            else -> super.collectLiveEvaluations(state)
        }
    }

    // handler of mlRepo in start (as a classification)
    fun onLiveClassificationStart (state: LiveClassificationState.Start)
}