package com.example.driverchecker

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import org.pytorch.*
import org.pytorch.torchvision.TensorImageUtils
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.net.URL

abstract class URLModel<T> : MLModel<T>() {
    protected var url: URL? = null

    fun setUrl(input_url: String) {
        url = URL(input_url)
    }
    override fun preProcess(data: T): T {
        return data
    }

    override fun evaluateData(data: T): String {
        return "not implemented yet"
    }


    // Function to encode the file found on the path in input.
    // attention: It doesn't check whether the path in input is correct or not
    private fun encodeImage (path: String) : String {
        return Base64.encodeToString(preProcessPhoto(path), Base64.DEFAULT)
    }


    private fun preProcessPhoto (path: String): ByteArray {
        val bm = BitmapFactory.decodeFile(path)
        val bmScaled = Bitmap.createScaledBitmap(bm, 1280, (bm.height*1280)/bm.width, true)
        val baos = ByteArrayOutputStream()
        bmScaled.compress(Bitmap.CompressFormat.JPEG, 100, baos) // bm is the bitmap object

        return baos.toByteArray()
    }
}