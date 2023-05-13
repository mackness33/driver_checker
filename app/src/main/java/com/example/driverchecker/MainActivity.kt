package com.example.driverchecker

import android.Manifest.permission
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.CameraX
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.example.driverchecker.databinding.ActivityMainBinding
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.runBlocking
import java.io.File
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ExecutorService

class MainActivity : AppCompatActivity() {

    class Picture (var uri: Uri?, var name: String?){
    }

    private var roboflow: Roboflow = Roboflow()
    private var imageRecognitionService: ImageRecognitionService = ImageRecognitionService()
    private var picture: Picture? = null
    private lateinit var viewBinding: ActivityMainBinding

    private var imageCapture: ImageCapture? = null

    private val cameraXHandler: CameraXHandler = CameraXHandler()

    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    // companion object
    companion object {
        private val PERMISSION_CODE = 1002
        internal val DEBUG_TAG = "MainActivity"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS =
            mutableListOf (
                permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P) {
                    add(permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
        private val PICTURE_FILE_NAME: String = "driver_checker"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        // Request camera permissions
        if (allPermissionsGranted()) {
            cameraXHandler.startCamera(this, viewBinding.viewFinder.surfaceProvider)
        } else {
            ActivityCompat.requestPermissions(
                this, REQUIRED_PERMISSIONS, 10)
        }

        viewBinding.btnChoose.setOnClickListener {
            val permissions = arrayOf(permission.READ_EXTERNAL_STORAGE, permission.INTERNET)
            if (checkPermissions(permissions)) {
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                chooseImageGallery()
            }
        }

        viewBinding.btnTakeWithCamera.setOnClickListener{
            val permissions = arrayOf(permission.WRITE_EXTERNAL_STORAGE, permission.CAMERA)
            if (checkPermissions(permissions)) {
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                takePhotoWithCamera()
            }
        }

        viewBinding.btnTake.setOnClickListener{
            val permissions = arrayOf(permission.WRITE_EXTERNAL_STORAGE, permission.CAMERA, permission.INTERNET)
            if (checkPermissions(permissions)) {
                requestPermissions(permissions, PERMISSION_CODE)
            } else {
                cameraXHandler.takePhoto(this, FILENAME_FORMAT, PICTURE_FILE_NAME, imageRecognitionService)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
//        if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
//            Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show()
//            Snackbar.make(
//                findViewById(android.R.id.content),
//                R.string.storage_permission_denied_message,
//                Snackbar.LENGTH_LONG)
//                .show()
//        }

        if (allPermissionsGranted()) {
            cameraXHandler.startCamera(this, viewBinding.viewFinder.surfaceProvider)
        } else {
            Toast.makeText(this,
                "Permissions not granted by the user.",
                Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun checkPermissions (permissions: Array<String>) : Boolean {
        for (perm: String in permissions) {
            if (checkSelfPermission(perm) == PackageManager.PERMISSION_DENIED) {
                return true
            }
        }

        return false
    }

    var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            // There are no request codes
            val data: Intent? = result.data
            if (data != null && data.data != null) {
                val path: String? = FileUtils.getPath(data.data!!, this@MainActivity)

                if (path != null) {
                    viewBinding.imgView.setImageURI(picture?.uri)
                    imageRecognitionService.makePrediction(path, true)

                }
            }

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

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            baseContext, it) == PackageManager.PERMISSION_GRANTED
    }
}
