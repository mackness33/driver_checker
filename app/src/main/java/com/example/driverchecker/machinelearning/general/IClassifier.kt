package com.example.driverchecker.machinelearning.general

import com.example.driverchecker.machinelearning.data.Classification

interface IClassifier {
    fun add(name: String)

    fun asList() : List<Classification>
    fun asSortedList() : List<Classification>

    fun checkName(name: String) : Boolean
    fun checkIndex(index: Int) : Boolean
    fun check(classification: Classification) : Boolean

    fun clean(superclass: Boolean?)

    fun remove (name: String)
    fun remove (index: Int)

    fun getIndex (name: String) : Int
    fun getName (index: Int) : String

    fun getClassification (name: String) : Classification
    fun getClassification (index: Int) : Classification

    fun exist (name: String) : Boolean
    fun exist (index: Int) : Boolean
    fun exist (classification: Classification) : Boolean

    fun getSuperclass (isDriver: Boolean) : List<Classification>
}