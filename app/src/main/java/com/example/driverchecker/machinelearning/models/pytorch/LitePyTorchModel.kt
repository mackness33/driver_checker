package com.example.driverchecker.machinelearning.models.pytorch

import android.util.Log
import com.example.driverchecker.machinelearning.models.MachineLearningModel
import kotlinx.coroutines.CoroutineScope
import org.pytorch.LiteModuleLoader
import org.pytorch.Module

abstract class LitePyTorchModel <I, O> (scope: CoroutineScope) : MachineLearningModel<I, O>(scope) {
    protected var module: Module? = null

    constructor(modelPath: String? = null, scope: CoroutineScope) : this(scope) {
        if (modelPath != null) initModel(modelPath)
    }

    protected fun initModel(json: String?) = if (!json.isNullOrEmpty()) loadModel(json) else null

    override fun <String> loadModel (init: String) {
        try {
            // loading serialized torchscript module from packaged into app android asset model.ptl,
            // app/src/model/assets/model.ptl
            val newModule = LiteModuleLoader.load(init.toString())
            mIsLoaded.value = true
//            modelStateProducer.modelReady(true)
            module = newModule
        } catch (e: Throwable) {
            Log.e("LitePyTorch", e.message ?: "The model couldn't be loaded")
            mIsLoaded.value = false
        }
    }
}