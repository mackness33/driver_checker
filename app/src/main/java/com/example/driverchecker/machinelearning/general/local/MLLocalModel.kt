package com.example.driverchecker.machinelearning.general.local

import android.graphics.Rect
import android.util.Log
import com.example.driverchecker.machinelearning.general.MLModel
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import java.io.IOException
import java.util.*
import kotlin.math.max

abstract class MLLocalModel <Data, Result> (modelPath: String? = null) : MLModel<Data, Result>(){
    protected var module: Module? = null

    init {
        if (modelPath != null)
            loadModel(modelPath)
    }

    final override fun loadModel(uri: String) {
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