package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.IMachineLearningFinalResult
import com.example.driverchecker.machinelearning.data.WithConfidence

interface IMachineLearningWindow<E : WithConfidence> : WithConfidence {
    fun getIndex() : Int

    fun getLastResult() : E?

    fun totalNumber() : Int

    fun isSatisfied() : Boolean

    fun next (element: E)

    fun clean ()

    fun getFinalResults() : IMachineLearningFinalResult
}
