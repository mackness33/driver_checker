//package com.example.driverchecker
//
//import android.util.Log
//import com.example.driverchecker.machinelearning.general.MLModel
//import org.pytorch.LiteModuleLoader
//import org.pytorch.Module
//import java.io.IOException
//
//abstract class PyModel <T> : MLModel<T>(){
//    protected var module: Module? = null
//
//    fun loadModel(path: String) {
//        try {
//            // loading serialized torchscript module from packaged into app android asset model.pt,
//            // app/src/model/assets/model.pt
//            module = LiteModuleLoader.load(path)
//            isLoaded = true
//        } catch (e: IOException) {
////            Log.e("ImageDetection", "Error loading model!", e)
//        }
//    }
//}