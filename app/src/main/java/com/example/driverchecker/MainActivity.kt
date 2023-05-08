package com.example.driverchecker

import PhotoHandler
import android.Manifest.permission
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.ImageFormat
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
//import androidx.camera.view.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.driverchecker.databinding.ActivityMainBinding
import com.google.android.material.snackbar.Snackbar
import java.io.File
import java.util.concurrent.ExecutorService

class MainActivity : AppCompatActivity() {

    class Picture (var uri: Uri?, var name: String?){
//        var uri: Uri?
//        var name: String?

//        constructor (inputName: String?, inputUri: Uri?) {
//            name = inputName
//            uri = inputUri
//        }
    }


    private lateinit var textView: TextView
    private lateinit var buttonChoose: Button
    private lateinit var imageView: ImageView
    private lateinit var buttonTakeWithCamera: Button
    private lateinit var buttonTake: Button
    private var picture: Picture? = null
    private val PICTURE_FILE_NAME: String = "driver_checker.jpg"
    private var cameraId = 0

    private lateinit var viewBinding: ActivityMainBinding

    private var imageCapture: ImageCapture? = null

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    private lateinit var cameraExecutor: ExecutorService


    // companion object
    companion object {
        private val PICK_IMAGE = 1000
        private val TAKE_PIC_WITH_CAMERA = 1001
        private val PERMISSION_CODE = 1002
        internal val DEBUG_TAG = "MainActivity"
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                permission.CAMERA,
                permission.RECORD_AUDIO
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        val view: View = viewBinding.root
        setContentView(view)

        // Set up the listeners for take photo and video capture buttons
//        viewBinding.imageCaptureButton.setOnClickListener { takePhoto() }
//        viewBinding.videoCaptureButton.setOnClickListener { captureVideo() }

        viewBinding.btnChoose.setOnClickListener {
            checkAndRequestPermissions(arrayOf(permission.READ_EXTERNAL_STORAGE), ::chooseImageGallery)
        }

        viewBinding.btnTakeWithCamera.setOnClickListener {
            checkAndRequestPermissions(arrayOf(permission.WRITE_EXTERNAL_STORAGE, permission.CAMERA), ::takePhotoWithCamera)
        }

//        viewBinding.btnTake.setOnClickListener {
//            camera!!.startPreview();
//            camera!!.takePicture(null, null, PhotoHandler(getApplicationContext()));
//        }

        viewBinding.btnTake.setOnClickListener { checkAndRequestPermissions(arrayOf(permission.WRITE_EXTERNAL_STORAGE, permission.CAMERA), ::startCamera) }

//        openCamera()

        // "context" must be an Activity, Service or Application object from your app.
        if (! Python.isStarted()) {
            Python.start(AndroidPlatform(baseContext))
        }

        // need to create a singleton of Python instance to run the various script with it in the whole proj.
        // Create Python instance
        val py: Python = Python.getInstance()

        // Create Python object
        val pyObj: PyObject = py.getModule("testPy")

        /// call the function
        val obj: PyObject = pyObj.callAttr("main")

        // now set returned text to textview
//        textView.text = obj.toString()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
            Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show()
            Snackbar.make(
                findViewById(android.R.id.content),
                R.string.storage_permission_denied_message,
                Snackbar.LENGTH_LONG)
                .show()
        }
    }


    override fun onDestroy() {
        super.onDestroy()
        cameraExecutor.shutdown()
    }

    private fun checkPermissions (permissions: Array<String>) : Boolean {
        for (perm: String in permissions) {
            if (checkSelfPermission(perm) == PackageManager.PERMISSION_DENIED) {
                return true
            }
        }

        return false
    }

    private fun checkAndRequestPermissions(permissions: Array<String>, action: () -> Unit) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkPermissions(permissions)){
                requestPermissions(permissions, PERMISSION_CODE)
            } else{
                action()
            }
        } else{
            action()
        }
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            if (data != null && data.data != null) {
                picture = Picture(data.data, "")
            }
        }

        if (picture != null && picture?.uri != null) {
            viewBinding.imgView.setImageURI(picture?.uri)
        }
    }


    private fun chooseImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        intent.putExtra("code", "PICK_IMAGE")
        resultLauncher.launch(intent)
    }

    private fun takePhotoWithCamera() {
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val providerFile = FileProvider.getUriForFile(this,"com.example.driverchecker.fileprovider", this.getPhotoFile(PICTURE_FILE_NAME))
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerFile)
        picture = Picture(providerFile, PICTURE_FILE_NAME)

        resultLauncher.launch(takePhotoIntent)
    }

    private fun getPhotoFile(fileName: String): File {
        val directoryStorage = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, "", directoryStorage)
    }

//    private fun findFrontFacingCamera(): Int {
//        var cameraId = -1
//        // Search for the front facing camera
//        val numberOfCameras: Int = Camera.getNumberOfCameras()
//        for (i in 0 until numberOfCameras) {
//            val info = Camera.CameraInfo()
//            Camera.getCameraInfo(i, info)
//            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
//                Log.d(MainActivityCopy.DEBUG_TAG, "Camera found")
//                cameraId = i
//                break
//            }
//        }
//        return cameraId
//    }

//    private fun openCamera () {
//        // do we have a camera?
//        if (!packageManager
//                .hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
//            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
//                .show();
//        } else {
//            cameraId = findFrontFacingCamera();
//            if (cameraId < 0) {
//                Toast.makeText(this, "No front facing camera found.",
//                    Toast.LENGTH_LONG).show();
//            } else {
//                if ( camera != null) {
//                    camera = Camera.open(cameraId);
//                }
//            }
//        }
//    }

//    private fun startCameraSession() {
//        val cameraManager = getSystemService(Context.CAMERA_SERVICE) as CameraManager
//
//        if (cameraManager.cameraIdList.isEmpty()) {
//            // no cameras
//            return
//        }
//
////        val firstCamera = cameraIdList[0]
//
//        if (ActivityCompat.checkSelfPermission(
//                this,
//                permission.CAMERA
//            ) != PackageManager.PERMISSION_GRANTED
//        ) {
//            // TODO: Consider calling
//            //    ActivityCompat#requestPermissions
//            // here to request the missing permissions, and then overriding
//            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
//            //                                          int[] grantResults)
//            // to handle the case where the user grants the permission. See the documentation
//            // for ActivityCompat#requestPermissions for more details.
//            return
//        }
//        cameraManager.openCamera(firstCamera, object: CameraDevice.StateCallback() {
//            override fun onDisconnected(p0: CameraDevice) { }
//            override fun onError(p0: CameraDevice, p1: Int) { }
//
//            override fun onOpened(cameraDevice: CameraDevice) {
//                // use the camera
//                val cameraCharacteristics = cameraManager.getCameraCharacteristics(cameraDevice.id)
//
//                cameraCharacteristics[CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP]?.let { streamConfigurationMap ->
//                    streamConfigurationMap.getOutputSizes(ImageFormat.JPEG)?.let { yuvSizes ->
//                        val previewSize = yuvSizes.last()
//
//                    }
//
//                }
//            }
//        }, Handler { true })
//    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)

        cameraProviderFuture.addListener({
            // Used to bind the lifecycle of cameras to the lifecycle owner
            val cameraProvider: ProcessCameraProvider = cameraProviderFuture.get()

            // Preview
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewBinding.viewFinder.surfaceProvider)
                }

            // Select back camera as a default
            val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview)

            } catch(exc: Exception) {
                Log.e("CameraXErr", "Use case binding failed", exc)
            }

        }, ContextCompat.getMainExecutor(this))
    }
}
