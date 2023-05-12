package com.example.driverchecker

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.StandardCharsets
import javax.net.ssl.HttpsURLConnection


// todo: create the stream to show the boxes on live stream
class ImageRecognitionService {

    public fun makePrediction (path: String, type: Boolean){
        if (type) makeReqToExternalUri(path)
    }

    // Make a request to an external url to get the prediction of the image in input
    private fun makeReqToExternalUri (path: String) {
        // todo: get the url from the os
        val url = URL("https://detect.roboflow.com/checker-ei67f/1?api_key=R6X2vkBZa49KTGoYyv9y")

        // create the https connection
        val https = url.openConnection() as HttpsURLConnection

        https.requestMethod = "POST"
        https.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        https.doInput = true
        https.doOutput = true

        // encode the photo
        val encodedImage = encodeImage (path)
        val byteArrayEncodedImage = encodedImage.toByteArray(StandardCharsets.US_ASCII)
        https.setRequestProperty("Content-Length", byteArrayEncodedImage.size.toString())

        // send the stream of data
        val dataOutputStream = DataOutputStream(https.outputStream)
        dataOutputStream.write(byteArrayEncodedImage)

        // get the resopnse and process it
        val responseStream = if (https.responseCode == HttpsURLConnection.HTTP_OK) https.inputStream else https.errorStream
        val  reader = BufferedReader(InputStreamReader(responseStream))
        var line: String?
        while (reader.readLine().also { line = it } != null) {
            // here we're gonna save the result of the prediction
            // todo: get and use the result of the prediction
            println(line)
        }

        // close the streams
        reader.close()
        dataOutputStream.close()
    }

    // Function to encode the file found on the path in input.
    // attention: It doesn't check whether the path in input is correct or not
    private fun encodeImage (path: String) : String {
        val bm = BitmapFactory.decodeFile(path)
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos) // bm is the bitmap object

        return Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
    }
}