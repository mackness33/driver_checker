package com.example.driverchecker

import android.content.ContentValues
import android.content.Context
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.Preview.SurfaceProvider
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.nio.ByteBuffer
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.Executors
import javax.security.auth.callback.Callback

typealias ImageDetectionListener = (image: ImageProxy) -> Unit

class CameraXHandler (){
    private var imageCapture: ImageCapture? = null
    private var imageAnalyzer: ImageAnalysis? = null
    var hasCameraStarted: Boolean = false
        private set

    fun startCamera(context: Context, surfaceProvider: SurfaceProvider, listener: ImageDetectionListener, model: CameraViewModel) {
        hasCameraStarted = false
        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().apply {
                setAnalyzer(Executors.newFixedThreadPool(1), ImageDetectionAnalyzer (listener))
//                    runBlocking {
//                        val yBuffer = it.planes[0].buffer // Y
//                        val vuBuffer = it.planes[2].buffer // VU
//
//                        val ySize = yBuffer.remaining()
//                        val vuSize = vuBuffer.remaining()
//
//                        val nv21 = ByteArray(ySize + vuSize)
//
//                        yBuffer.get(nv21, 0, ySize)
//                        vuBuffer.get(nv21, ySize, vuSize)
//
//                        val yuvImage = YuvImage(nv21, ImageFormat.NV21, it.width, it.height, null)
//                        val out = ByteArrayOutputStream()
//                        yuvImage.compressToJpeg(Rect(0, 0, yuvImage.width, yuvImage.height), 50, out)
//                        val imageBytes = out.toByteArray()
//                        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
//                        model.nextFrame(bitmap)
//                    }
//                })
            }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                if (context is AppCompatActivity) {
                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(context, cameraSelector, preview, imageCapture, imageAnalyzer);
                }

                hasCameraStarted = true

            } catch(exc: Exception) {
                Log.e("CameraXError", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(context))
    }


    fun takePhoto(context: Context, fileFormat: String, fileName: String, model: CameraViewModel) {
        // Get a stable reference of the modifiable image capture use case
        val imageCapture = imageCapture ?: return

        // Create time stamped name and MediaStore entry.
        val name = SimpleDateFormat(fileFormat, Locale.US).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
            }
        }

        // Create output options object which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(context.contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues)
            .build()

        // Set up image capture listener, which is triggered after photo has been taken
        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(context),
            object : ImageCapture.OnImageSavedCallback {
                override fun onError(exc: ImageCaptureException) {
                    Log.e("CameraXError", "Photo capture failed: ${exc.message}", exc)
                }

                override fun
                        onImageSaved(output: ImageCapture.OutputFileResults){

                    if (output.savedUri != null) {
                        val path = FileUtils.getPath(output.savedUri!!, context)
                        model.updateImageUri(output.savedUri)
                        if (path != null) model.updatePath(path)
                    }
                    val msg = "Photo capture succeeded: ${output.savedUri}"
                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                    Log.d("CameraXError", msg)
                }
            }
        )
    }

    fun pauseCamera () {
        hasCameraStarted = false
    }

    private class ImageDetectionAnalyzer(private val listener: ImageDetectionListener) : ImageAnalysis.Analyzer {

        private fun ByteBuffer.toByteArray(): ByteArray {
            rewind()    // Rewind the buffer to zero
            val data = ByteArray(remaining())
            get(data)   // Copy the buffer into a byte array
            return data // Return the byte array
        }

        override fun analyze(image: ImageProxy) {

//            val buffer = image.planes[0].buffer
//            val data = buffer.toByteArray()
//            val pixels = data.map { it.toInt() and 0xFF }
//            val luma = pixels.average()
//            Log.i("Analyze", "it is analyzing yess")
            listener(image)
//            Log.i("Analyze", image.format.toString())
            image.close()
        }
    }

}