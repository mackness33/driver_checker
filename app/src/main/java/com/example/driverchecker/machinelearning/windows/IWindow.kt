package com.example.driverchecker.machinelearning.windows

import com.example.driverchecker.machinelearning.data.*

interface IWindow <E> {
    val hasAcceptedLast: Boolean
    val totalElements: Int
    val lastResult: E?

    fun isSatisfied() : Boolean
    fun next (element: E, timeOffset: Double?)
    suspend fun clean ()
}