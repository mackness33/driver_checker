package com.example.driverchecker

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import kotlinx.coroutines.*
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.StandardCharsets
import javax.net.ssl.HttpsURLConnection


// todo: create the stream to show the boxes on live stream
abstract class MLService<T> {
    protected var pyModel: PyModel<T>? = null
    protected var urlModel: URLModel<T>? = null

    fun loadModel (path: String) {
        pyModel?.loadModel(path)
    }

    fun setUrlModel (url: String) {
        urlModel?.setUrl(url)
    }

    protected fun analyzeData (data: T, isOnline: Boolean) : String? {
        return if (isOnline) urlModel?.analyzeData(data) else pyModel?.analyzeData(data)
    }

    abstract fun analyzeData(path: String, isOnline: Boolean): String
}