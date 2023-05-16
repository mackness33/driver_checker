package com.example.driverchecker

import android.Manifest.permission
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.video.Recorder
import androidx.camera.video.Recording
import androidx.camera.video.VideoCapture
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.example.driverchecker.databinding.ActivityMainBinding
import java.util.*

class MainActivity : AppCompatActivity() {
    private val imageRecognitionService: ImageRecognitionService = ImageRecognitionService()
    private lateinit var viewBinding: ActivityMainBinding
    private val cameraXHandler: CameraXHandler = CameraXHandler()
    private var videoCapture: VideoCapture<Recorder>? = null
    private var recording: Recording? = null

    // companion object
    companion object {
        private val PERMISSION_CODE = 1002
        internal val DEBUG_TAG = "MainActivity"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        private val REQUIRED_PERMISSIONS_CHOOSE_PHOTO =
            arrayOf(permission.WRITE_EXTERNAL_STORAGE, permission.CAMERA, permission.INTERNET)
        private val REQUIRED_PERMISSIONS_TAKE_PHOTO =
            arrayOf(permission.WRITE_EXTERNAL_STORAGE, permission.CAMERA, permission.INTERNET)
        private val PICTURE_FILE_NAME: String = "driver_checker"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .setReorderingAllowed(true)
                .add(R.id.fragment_container_view, CameraFragment.newInstance(), "Camera")
                .commit()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (allCameraXPermissionsGranted()) {
//            cameraXHandler.startCamera(this, viewBinding.viewFinder.surfaceProvider)
        } else {
            Toast.makeText(
                this,
                "Permissions not granted by the user.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun checkPermissions(permissions: Array<String>): Boolean {
        for (perm: String in permissions) {
            if (checkSelfPermission(perm) == PackageManager.PERMISSION_DENIED) {
                return true
            }
        }

        return false
    }

    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                if (data != null && data.data != null && data.data is Uri) {
                    val path: String? = FileUtils.getPath(data.data as Uri, this@MainActivity)

                    if (path != null) {
//                        viewBinding.imgView.setImageURI(data.data)
                        imageRecognitionService.awaitPrediction(path, true)
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

    private fun allCameraXPermissionsGranted() = REQUIRED_PERMISSIONS_TAKE_PHOTO.all {
        ContextCompat.checkSelfPermission(
            baseContext, it
        ) == PackageManager.PERMISSION_GRANTED
    }
}
