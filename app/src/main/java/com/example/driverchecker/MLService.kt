//package com.example.driverchecker
//
//import com.example.driverchecker.machinelearning.general.MLModel
//import kotlinx.coroutines.*
//import java.util.concurrent.Callable
//
//
//// todo: create the stream to show the boxes on live stream
//abstract class MLService<Data, Result> (protected var mlModel: MLModel<Data, Result>){
//
//    open inner class EvaluationTask (private val input: Data) : Callable<Result> {
//        override fun call(): Result? {
//            return mlModel.processAndEvaluate(input)
//        }
//    }
//
//
//
//
//    fun loadModel (path: String) {
//        pyModel?.loadModel(path)
//    }
//
//    fun setUrlModel (url: String) {
//        urlModel?.setUrl(url)
//    }
//
//    fun analyzeData (data: T, isOnline: Boolean) : String? {
//        return if (isOnline) urlModel?.analyzeData(data) else pyModel?.analyzeData(data)
//    }
//}