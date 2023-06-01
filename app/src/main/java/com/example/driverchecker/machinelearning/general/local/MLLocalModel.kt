package com.example.driverchecker.machinelearning.general.local

import android.util.Log
import com.example.driverchecker.machinelearning.general.MLModel
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import java.io.IOException
import java.util.concurrent.Callable

abstract class MLLocalModel <Data, Result> (private val modelPath: String? = null) : MLModel<Data, Result>(modelPath){
    protected var module: Module? = null

    override fun loadModel(uri: String) {
        try {
            // loading serialized torchscript module from packaged into app android asset model.pt,
            // app/src/model/assets/model.pt
            module = LiteModuleLoader.load(uri)
            isLoaded = true
        } catch (e: IOException) {
            Log.e("ImageDetection", "Error loading model!", e)
        }
    }
}