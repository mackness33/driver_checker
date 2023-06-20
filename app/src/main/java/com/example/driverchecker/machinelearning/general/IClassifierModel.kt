package com.example.driverchecker.machinelearning.general.local

import com.example.driverchecker.machinelearning.general.MLModelInterface

interface IClassifierModel<Data, Result> : MLModelInterface<Data, Result>{
    fun loadClassification 
}