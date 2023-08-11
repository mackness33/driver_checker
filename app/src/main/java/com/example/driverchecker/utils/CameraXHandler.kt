package com.example.driverchecker.utils

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.*
import androidx.camera.core.Preview.SurfaceProvider
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import java.util.concurrent.Executors

typealias LiveCameraXListener = (image: ImageProxy) -> Unit

class CameraXHandler (){
//    private var imageCapture: ImageCapture? = null
//    private var videoCapture: VideoCapture<Recorder>? = null
//    private var recording: Recording? = null

    private var imageAnalyzer: ImageAnalysis? = null
    var hasCameraStarted: Boolean = false
        private set

    fun startCamera(context: Context, surfaceProvider: SurfaceProvider, listener: LiveCameraXListener) {
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

//            imageCapture = ImageCapture.Builder().build()

            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build().apply {
                setAnalyzer(Executors.newSingleThreadExecutor(), ImageDetectionAnalyzer(listener))}

//            val recorder = Recorder.Builder()
//                .setQualitySelector(QualitySelector.from(Quality.SD))
//                .build()

//            videoCapture = VideoCapture.withOutput(recorder)

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                if (context is AppCompatActivity) {
                    // Bind use cases to camera
                    cameraProvider.bindToLifecycle(context, cameraSelector, preview, imageAnalyzer);
                }

                hasCameraStarted = true

            } catch(exc: Exception) {
                Log.e("CameraXError", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(context))
    }

    fun pauseCamera () {
        hasCameraStarted = false
    }

    private class ImageDetectionAnalyzer(private val listener: LiveCameraXListener) : ImageAnalysis.Analyzer {
        override fun analyze(image: ImageProxy) {
            listener(image)
        }
    }

//    fun takePhoto(context: Context, fileFormat: String, fileName: String, model: CameraViewModel) {
//        // Get a stable reference of the modifiable image capture use case
//        val imageCapture = imageCapture ?: return
//
//        // Create time stamped name and MediaStore entry.
//        val name = SimpleDateFormat(fileFormat, Locale.US).format(System.currentTimeMillis())
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
//            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
//            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
//            }
//        }
//
//        // Create output options object which contains file + metadata
//        val outputOptions = ImageCapture.OutputFileOptions
//            .Builder(context.contentResolver,
//                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                contentValues)
//            .build()
//
//        // Set up image capture listener, which is triggered after photo has been taken
////        imageCapture.takePicture(
////            outputOptions,
////            ContextCompat.getMainExecutor(context),
////            object : ImageCapture.OnImageSavedCallback {
////                override fun onError(exc: ImageCaptureException) {
////                    Log.e("CameraXError", "Photo capture failed: ${exc.message}", exc)
////                }
////
////                override fun
////                        onImageSaved(output: ImageCapture.OutputFileResults){
////
////                    if (output.savedUri != null) {
////                        val path = FileUtils.getPath(output.savedUri!!, context)
////                        model.updateImageUri(output.savedUri)
////                        if (path != null) model.updatePath(path)
////                    }
////                    val msg = "Photo capture succeeded: ${output.savedUri}"
////                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
////                    Log.d("CameraXError", msg)
////                }
////            }
////        )
//    }

    // Implements VideoCapture use case, including start and stop capturing.
//    fun captureVideo(context: Context, fileFormat: String, model: CameraViewModel, onFinalize: () -> Unit) {
//        val videoCapture = this.videoCapture ?: return
//
////        binding.btnRecordVideo.isEnabled = false
//        model.enableVideo(false)
//
//        val curRecording = recording
//        if (curRecording != null) {
//            // Stop the current recording session.
//            curRecording.stop()
//            recording = null
//            return
//        }
//
//        // create and start a new recording session
//        val name = SimpleDateFormat(fileFormat, Locale.US).format(System.currentTimeMillis())
//        val contentValues = ContentValues().apply {
//            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
//            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
//            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
//                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/CameraX-Image")
//            }
//        }
//
//        val mediaStoreOutputOptions = MediaStoreOutputOptions
//            .Builder(context.contentResolver, MediaStore.Video.Media.EXTERNAL_CONTENT_URI)
//            .setContentValues(contentValues)
//            .build()
//
//        recording = videoCapture.output
//            .prepareRecording(context, mediaStoreOutputOptions)
//            .start(ContextCompat.getMainExecutor(context)) { recordEvent ->
//                when(recordEvent) {
//                    is VideoRecordEvent.Start -> {
//                        model.enableVideo(true)
//                        model.recordVideo(true)
//                    }
//                    is VideoRecordEvent.Finalize -> {
//                        if (!recordEvent.hasError()) {
//                            val msg = "Video capture succeeded: " +
//                                    "${recordEvent.outputResults.outputUri}"
//                            Toast.makeText(context, msg, Toast.LENGTH_SHORT)
//                                .show()
//                            Log.d("CameraX/Video", msg)
//
//                            val path = FileUtils.getPath(recordEvent.outputResults.outputUri, context)
//                            model.updatePathVideo(path)
//
//                            onFinalize()
//                        } else {
//                            recording?.close()
//                            recording = null
//                            Log.e("CameraX/Video", "Video capture ends with error: " +
//                                    "${recordEvent.error}")
//                        }
//                        model.enableVideo(true)
//                        model.recordVideo(false)
//                    }
//                }
//            }
//    }

}