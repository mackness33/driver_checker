package com.example.driverchecker

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Environment
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.*
import java.net.URL
import java.nio.charset.StandardCharsets
import javax.net.ssl.HttpsURLConnection


class Roboflow {
    public fun makeReqToRoboflow (uri: Uri, context: Context) {
        val url = URL("https://detect.roboflow.com/checker-ei67f/1?api_key=R6X2vkBZa49KTGoYyv9y")

        val https = url.openConnection() as HttpsURLConnection

        https.requestMethod = "POST"
        https.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        https.doInput = true
        https.doOutput = true

        val data = encodeImage (FileUtilsKotlin.getPath(uri, context))
        val image = data.toByteArray(StandardCharsets.US_ASCII)
        https.setRequestProperty("Content-Length", image.size.toString())

        val dataOutputStream = DataOutputStream(https.outputStream)
        dataOutputStream.write(image)

        val reader = BufferedReader(InputStreamReader(if (https.responseCode == HttpsURLConnection.HTTP_OK) https.inputStream else https.errorStream))
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            println(line)
        }
        reader.close()
        dataOutputStream.close()

        // Check if the connection is successful
//        val responseCode = https.responseCode
//        if (responseCode == HttpsURLConnection.HTTP_OK) {
//            val response = https.inputStream.bufferedReader()
//                .use { it.readText() }  // defaults to UTF-8
////            withContext(Dispatchers.Main) {
////
////                // Convert raw JSON to pretty JSON using GSON library
////            }
//            Log.d("Pretty Printed JSON :", response)
//        } else {
//            val response = https.errorStream.bufferedReader()
//                .use { it.readText() }  // defaults to UTF-8
//            Log.e("HTTPURLCONNECTION_ERROR", responseCode.toString())
//            Log.e("HTTPURLCONNECTION_ERROR", response)
//        }

//        with (url.openConnection() as HttpsURLConnection) {
//            requestMethod = "POST"
//            println("\nSent 'POST' request to URL : $url; Response Code : $responseCode")
//
//
////            val postData: ByteArray = "hello".toByteArray(StandardCharsets.UTF_8)
//            val data  = encodeImage ("/storage/emulated/0/Pictures/img_0802.jpeg_20221010152117.jpg")
//
//            setRequestProperty("charset", "utf-8")
//            setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
//
//            doInput = true
//            doOutput = true
//
//            // Send the JSON we created
//            val outputStreamWriter = OutputStreamWriter(outputStream)
//            outputStreamWriter.write(data)
//            outputStreamWriter.flush()
//
//            inputStream.bufferedReader().use {
//                it.lines().forEach { line ->
//                    println(line)
//                }
//            }
//        }
    }

    private fun encodeImage (path: String?) : String {
        if (path == null) {
            return ""
        }
        val f = Environment.getExternalStorageDirectory()
        val bm = BitmapFactory.decodeFile(path)
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos) // bm is the bitmap object

        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
    }
}