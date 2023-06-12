package com.example.driverchecker.machinelearning.imagedetection

import android.graphics.Bitmap
import com.example.driverchecker.machinelearning.data.ImageDetectionBox
import com.example.driverchecker.machinelearning.data.ImageDetectionInput
import com.example.driverchecker.machinelearning.data.MLMetrics
import com.example.driverchecker.machinelearning.data.MLResult
import com.example.driverchecker.machinelearning.general.remote.MLRemoteModel

class ImageDetectionRemoteModel (private val modelPath: String? = null) :  MLRemoteModel<ImageDetectionInput, ImageDetectionArrayResult>(modelPath){
    override fun preProcess(data: ImageDetectionInput): ImageDetectionInput {
        val resizedBitmap = Bitmap.createScaledBitmap(data.image, 640, 640, true)
        return ImageDetectionInput(resizedBitmap, data.scale, data.vector, data.start)
    }

    override fun evaluateData(input: ImageDetectionInput): ImageDetectionArrayResult {
        // todo: get the url from the os
//        val url = URL("https://detect.roboflow.com/checker-ei67f/1?api_key=R6X2vkBZa49KTGoYyv9y")
//
//        // create the https connection
//        val https = url.openConnection() as HttpsURLConnection
//
//        https.requestMethod = "POST"
//        https.setRequestProperty("Content-Type", "application/x-www-form-urlencoded")
//        https.doInput = true
//        https.doOutput = true
//
//        // encode the photo
//
//        val baos = ByteArrayOutputStream()
//        input.compress(Bitmap.CompressFormat.JPEG, 100, baos) // bm is the bitmap object
//        val encodedImage = Base64.encodeToString(baos.toByteArray(), Base64.DEFAULT)
//        val byteArrayEncodedImage = encodedImage.toByteArray(StandardCharsets.US_ASCII)
//        https.setRequestProperty("Content-Length", byteArrayEncodedImage.size.toString())
//
//        // send the stream of data
//        val dataOutputStream = DataOutputStream(https.outputStream)
//        dataOutputStream.write(byteArrayEncodedImage)
//
//        // get the response and process it
//        val responseStream = if (https.responseCode == HttpsURLConnection.HTTP_OK) https.inputStream else https.errorStream
//        val  reader = BufferedReader(InputStreamReader(responseStream))
//        var line: String?
//        var result: String = ""
//        while (reader.readLine().also { line = it } != null) {
//            // here we're gonna save the result of the prediction
//            // todo: get and use the result of the prediction
//            println(line)
//            result += line
//        }
//
//        // close the streams
//        reader.close()
//        dataOutputStream.close()

        return ArrayList()
    }

    override fun postProcess(output: ImageDetectionArrayResult): ImageDetectionArrayResult {
//        TODO("Not yet implemented")
        return output
    }
}