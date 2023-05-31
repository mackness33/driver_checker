package com.example.driverchecker.machinelearning.imagedetection

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import com.example.driverchecker.machinelearning.data.MLMetrics
import com.example.driverchecker.machinelearning.data.MLResult
import com.example.driverchecker.machinelearning.general.local.MLLocalModel
import com.example.driverchecker.machinelearning.general.remote.MLRemoteModel
import org.pytorch.*
import org.pytorch.torchvision.TensorImageUtils
import java.io.BufferedReader
import java.io.ByteArrayOutputStream
import java.io.DataOutputStream
import java.io.InputStreamReader
import java.net.URL
import java.nio.charset.StandardCharsets
import javax.net.ssl.HttpsURLConnection

class ImageDetectionRemoteModel (private val modelPath: String? = null) :  MLRemoteModel<Bitmap, MLResult<Float>>(modelPath){
    override fun preProcess(data: Bitmap): Bitmap {
        return Bitmap.createScaledBitmap(data, 1280, (data.height*1280)/data.width, true)
    }

    override fun evaluateData(input: Bitmap): MLResult<Float> {
        // todo: get the url from the os
        val url = URL("https://detect.roboflow.com/checker-ei67f/1?api_key=R6X2vkBZa49KTGoYyv9y")

        // create the https connection
        val https = url.openConnection() as HttpsURLConnection

        https.requestMethod = "POST"
        https.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
        https.doInput = true
        https.doOutput = true

        // encode the photo

        val baos = ByteArrayOutputStream()
        input.compress(Bitmap.CompressFormat.JPEG, 100, baos) // bm is the bitmap object
        val encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
        val byteArrayEncodedImage = encodedImage.toByteArray(StandardCharsets.US_ASCII)
        https.setRequestProperty("Content-Length", byteArrayEncodedImage.size.toString())

        // send the stream of data
        val dataOutputStream = DataOutputStream(https.outputStream)
        dataOutputStream.write(byteArrayEncodedImage)

        // get the response and process it
        val responseStream = if (https.responseCode == HttpsURLConnection.HTTP_OK) https.inputStream else https.errorStream
        val  reader = BufferedReader(InputStreamReader(responseStream))
        var line: String?
        var result: String = ""
        while (reader.readLine().also { line = it } != null) {
            // here we're gonna save the result of the prediction
            // todo: get and use the result of the prediction
            println(line)
            result += line
        }

        // close the streams
        reader.close()
        dataOutputStream.close()

        return MLResult(0F, MLMetrics())
    }

    override fun postProcess(output: MLResult<Float>): MLResult<Float> {
//        TODO("Not yet implemented")
        return output
    }
}