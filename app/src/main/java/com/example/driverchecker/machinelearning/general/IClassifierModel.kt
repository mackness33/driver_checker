package com.example.driverchecker.machinelearning.general

interface IClassifierModel<Data, Result> : MLModelInterface<Data, Result> {
    fun loadClassification()
}