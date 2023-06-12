package com.example.driverchecker

import com.example.driverchecker.machinelearning.data.MLPrediction

interface MLWindowInterface<Element>{
    fun totalNumber() : Int

    fun isSatisfied() : Boolean

    fun next (element: Element)

    fun clean ()
}
