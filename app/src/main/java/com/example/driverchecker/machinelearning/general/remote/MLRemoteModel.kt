package com.example.driverchecker.machinelearning.general.remote

import android.util.Log
import com.example.driverchecker.machinelearning.general.MLModel
import org.pytorch.LiteModuleLoader
import org.pytorch.Module
import java.io.IOException
import java.net.URL

abstract class MLRemoteModel <Data, Result> (private val modelPath: String? = null) : MLModel<Data, Result>(modelPath){
    protected var externalURL: URL? = null

    override fun loadModel(uri: String) {
        externalURL = URL(uri)
        isLoaded = true
    }
}