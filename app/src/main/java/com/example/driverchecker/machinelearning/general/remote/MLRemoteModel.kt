package com.example.driverchecker.machinelearning.general.remote

import android.util.Log
import com.example.driverchecker.machinelearning.general.MLModel
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import java.io.IOException
import java.net.URL

abstract class MLRemoteModel <Data, Result> (modelPath: String? = null) : MLModel<Data, Result>(){
    protected var externalURL: URL? = null


    init {
        if (modelPath != null)
            loadModel(modelPath)
    }

    final override fun loadModel(uri: String) {
        externalURL = URL(uri)
        _isLoaded.value = true
    }
}