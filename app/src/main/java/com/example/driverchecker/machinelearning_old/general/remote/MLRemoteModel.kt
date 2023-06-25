package com.example.driverchecker.machinelearning_old.general.remote

import com.example.driverchecker.machinelearning_old.general.MLModel
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