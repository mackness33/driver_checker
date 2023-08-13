package com.example.driverchecker.machinelearning.models.pytorch

import android.util.Log
import com.example.driverchecker.machinelearning.models.MachineLearningModel
import org.pytorch.LiteModuleLoader
import org.pytorch.Module

abstract class LitePyTorchModel <Data, Result> () : MachineLearningModel<Data, Result>(){
    protected var module: Module? = null

    constructor(modelPath: String? = null) : this() {
        if (modelPath != null) initModel(modelPath)
    }

    protected fun initModel(json: String?) = if (!json.isNullOrEmpty()) loadModel(json) else null

    override fun <String> loadModel (init: String) {
        try {
            // loading serialized torchscript module from packaged into app android asset model.pt,
            // app/src/model/assets/model.pt
            val newModule = LiteModuleLoader.load(init.toString())
            mIsLoaded.value = true
            module = newModule
        } catch (e: Throwable) {
            Log.e("LitePyTorch", e.message ?: "The model couldn't be loaded")
        }
    }
}