package com.example.driverchecker

interface MLWindowInterface<Element>{
    fun totalNumber() : Int

    fun isSatisfied() : Boolean

    fun next (element: Element)

    fun clean ()
}
