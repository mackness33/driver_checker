package com.example.driverchecker

import PhotoHandler
import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.example.driverchecker.R.id.btnTake
import com.google.android.material.snackbar.Snackbar
import java.io.File


class MainActivityCopy : AppCompatActivity() {

    private lateinit var filePhoto: File
    private lateinit var textView: TextView
    private lateinit var buttonTakeWithCamera: Button
    private lateinit var buttonTake: Button
    private lateinit var buttonChoose: Button
    private lateinit var imageView: ImageView
    private lateinit var filePhotoUri: Uri
    private val FILENAME = "driver_checker.jpg"
    private var camera: Camera? = null
    private var cameraId = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.txtView)
        buttonTakeWithCamera = findViewById(R.id.btnView)
        buttonTake = findViewById(btnTake)
        buttonChoose = findViewById(R.id.btnChoose)
        imageView = findViewById(R.id.imgView)

        buttonTakeWithCamera.setOnClickListener {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){

                if (checkSelfPermission(CAMERA) == PackageManager.PERMISSION_DENIED){
                    val permissions = arrayOf(CAMERA)
                    requestPermissions(permissions, PERMISSION_CODE)
                } else{
                    takePhotoWithCamera()
                }
            }else{
                takePhotoWithCamera()
            }



            /*
            val intent = Intent()
            intent.putExtra("data", path.toURI())
            setResult(RESULT_OK, intent)
            */

            // startActivityForResult(takePhotoIntent, IMAGE_CHOOSE)

            takePhotoWithCamera()
        }

        buttonTakeWithCamera.setOnClickListener {
            camera!!.startPreview();
            camera!!.takePicture(null, null, PhotoHandler(getApplicationContext()));
        }

        buttonChoose.setOnClickListener {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                    val permissions = arrayOf(READ_EXTERNAL_STORAGE)
                    requestPermissions(permissions, PERMISSION_CODE)
                } else{
                    chooseImageGallery()
                }
            }else{
                chooseImageGallery()
            }
        }

        // do we have a camera?
        if (!getPackageManager()
                .hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            Toast.makeText(this, "No camera on this device", Toast.LENGTH_LONG)
                .show();
        } else {
            cameraId = findFrontFacingCamera();
            if (cameraId < 0) {
                Toast.makeText(this, "No front facing camera found.",
                    Toast.LENGTH_LONG).show();
            } else {
                if ( camera != null) {
                    camera = Camera.open(cameraId);
                }
            }
        }

        // "context" must be an Activity, Service or Application object from your app.
        if (! Python.isStarted()) {
            Python.start(AndroidPlatform(baseContext))
        }

        // need to create a singleton of Python instance to run the various script with it in the whole proj.
        // Create Python instance
        var py: Python = Python.getInstance()

        // Create Python object
        var pyObj: PyObject = py.getModule("testPy")

        /// call the function
        var obj: PyObject = pyObj.callAttr("main")

        // now set returned text to textview
        textView.text = obj.toString()
    }

    // companion object
    companion object {
        private val IMAGE_CHOOSE = 1000
        private val PERMISSION_CODE = 1001
        internal val DEBUG_TAG = "MainActivity"
    }

    private fun getPhotoFile(fileName: String): File {
        val directoryStorage = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, "driver_checker.jpg", directoryStorage)
    }

    private fun takePhotoWithCamera() {
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        filePhoto = this.getPhotoFile(FILENAME)
        val providerFile = FileProvider.getUriForFile(this,"com.example.driverchecker.fileprovider", filePhoto)
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerFile)
        filePhotoUri = FileProvider.getUriForFile(this,"com.example.driverchecker.fileprovider", filePhoto)

        imageView.setImageURI(filePhotoUri)
        /*
        takePhotoIntent.putExtra("data", providerFile);
        setResult(RESULT_OK,takePhotoYIntent);
        finish()
         */
        startActivity(takePhotoIntent)
    }

    private fun takePhoto() {
        val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        filePhoto = this.getPhotoFile(FILENAME)
        val providerFile = FileProvider.getUriForFile(this,"com.example.driverchecker.fileprovider", filePhoto)
        takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerFile)
        filePhotoUri = FileProvider.getUriForFile(this,"com.example.driverchecker.fileprovider", filePhoto)

        imageView.setImageURI(filePhotoUri)
        /*
        takePhotoIntent.putExtra("data", providerFile);
        setResult(RESULT_OK,takePhotoYIntent);
        finish()
         */
        startActivity(takePhotoIntent)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                IMAGE_CHOOSE -> if (data?.data !== null) filePhotoUri = data?.data!!
             }
        }

        imageView.setImageURI(filePhotoUri)
    }

    private fun chooseImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_CHOOSE)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when(requestCode){
            PERMISSION_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                    chooseImageGallery()
                } else {
                    Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show()
                    Snackbar.make(findViewById(android.R.id.content),
                        R.string.storage_permission_denied_message,
                        Snackbar.LENGTH_LONG)
                        .show()
                }
            }
        }
    }

    private fun findFrontFacingCamera(): Int {
        var cameraId = -1
        // Search for the front facing camera
        val numberOfCameras: Int = Camera.getNumberOfCameras()
        for (i in 0 until numberOfCameras) {
            val info = Camera.CameraInfo()
            Camera.getCameraInfo(i, info)
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                Log.d(DEBUG_TAG, "Camera found")
                cameraId = i
                break
            }
        }
        return cameraId
    }

    override fun onPause() {
        if (camera != null) {
            camera!!.release()
            camera = null
        }
        super.onPause()
    }
}