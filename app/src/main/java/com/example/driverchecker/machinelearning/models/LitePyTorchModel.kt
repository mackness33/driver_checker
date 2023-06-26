package com.example.driverchecker.machinelearning.models

import android.util.Log
import com.example.driverchecker.machinelearning_old.general.MLModel
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import java.io.IOException

abstract class LitePyTorchModel <Data, Result> (modelPath: String? = null) : MLModel<Data, Result>(){
    protected var module: Module? = null

    init {
        if (modelPath != null)
            loadModel(modelPath)
    }

    override fun loadModel(uri: String) {
        try {
            // loading serialized torchscript module from packaged into app android asset model.pt,
            // app/src/model/assets/model.pt
            val newModule = LiteModuleLoader.load(uri)
            _isLoaded.value = true
            module = newModule
        } catch (e: IOException) {
            Log.e("ImageDetection", e.message ?: "The model couldn't be loaded")
        }
    }

}