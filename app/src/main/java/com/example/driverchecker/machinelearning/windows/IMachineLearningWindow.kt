package com.example.driverchecker.machinelearning.general

interface IMachineLearningWindow<Element>{
    fun getIndex() : Int
    fun getLastResult() : Element?

    fun totalNumber() : Int

    fun isSatisfied() : Boolean

    fun next (element: Element)

    fun clean ()
}
