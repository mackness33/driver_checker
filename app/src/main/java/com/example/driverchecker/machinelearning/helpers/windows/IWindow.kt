package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.helpers.windows.helpers.IWindowTag

interface IWindow <E> : IMachineLearningFinalResultStats {
    val hasAcceptedLast: Boolean
    val totalElements: Int
    val lastResult: E?

    fun isSatisfied() : Boolean
    fun next (element: E, timeOffset: Double?)
    suspend fun clean ()
}