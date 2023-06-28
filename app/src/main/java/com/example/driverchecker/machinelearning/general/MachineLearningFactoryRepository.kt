package com.example.driverchecker.machinelearning.general

import android.graphics.Bitmap
import android.util.Log
import com.example.driverchecker.machinelearning.data.*
import com.example.driverchecker.machinelearning.pytorch.YOLOModel

abstract class MachineLearningFactoryRepository<Data, Result : WithConfidence>
    : MachineLearningRepository<Data, Result>, MachineLearningFactory<Data, Result> {

    constructor() : super(null)

    constructor(modelName: String, modelInit: Map<String, Any?>) : super(null){
        initUseRepo(modelName, modelInit)
    }

    protected fun initUseRepo (modelName: String, modelInit: Map<String, Any?>) = use (modelName, modelInit)
}