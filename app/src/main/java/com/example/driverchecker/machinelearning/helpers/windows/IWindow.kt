package com.example.driverchecker.machinelearning.helpers.windows

import com.example.driverchecker.machinelearning.data.*

interface IWindow <E> : IMachineLearningFinalResultStatsOld {
    val hasAcceptedLast: Boolean
    val totalElements: Int
    val lastResult: E?

    fun isSatisfied() : Boolean
    fun next (element: E, timeOffset: Double?)
    suspend fun clean ()
}