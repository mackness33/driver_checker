package com.example.driverchecker.machinelearning.models

import android.util.Log
import com.example.driverchecker.machinelearning.general.MachineLearningModel
import com.example.driverchecker.machinelearning_old.general.MLModel
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import java.io.IOException

abstract class LitePyTorchModel <Data, Result> () : MachineLearningModel<Data, Result>(){
    protected var module: Module? = null

    constructor(modelPath: String? = null) : this() {
        if (modelPath != null) loadModel(modelPath)
    }

    override fun <String> loadModel (init: String) {
        try {
            // loading serialized torchscript module from packaged into app android asset model.pt,
            // app/src/model/assets/model.pt
            val newModule = LiteModuleLoader.load(init.toString())
            _isLoaded.value = true
            module = newModule
        } catch (e: Throwable) {
            Log.e("LitePyTorch", e.message ?: "The model couldn't be loaded")
        }
    }
}