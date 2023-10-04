package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.*

interface IWindow <E> : IMachineLearningFinalResultStats {
    val hasAcceptedLast: Boolean
    val totalElement: Int
    val lastResult: E?

    fun isSatisfied() : Boolean
    fun next (element: E, timeOffset: Double?)
    suspend fun clean ()
}