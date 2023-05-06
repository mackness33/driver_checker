package com.example.driverchecker

import android.Manifest.permission
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.FileProvider
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.android.material.snackbar.Snackbar
import java.io.File

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
    private lateinit var visiblePicture: Button
    private var picture: Picture? = null
    private val PICTURE_FILE_NAME: String = "driver_checker.jpg"

    // companion object
    companion object {
        private val PICK_IMAGE = 1000
        private val TAKE_PIC_WITH_CAMERA = 1001
        private val PERMISSION_CODE = 1002
        internal val DEBUG_TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.txtView)
        buttonChoose = findViewById(R.id.btnChoose)
        imageView = findViewById(R.id.imgView)
        buttonTakeWithCamera = findViewById(R.id.btnTakeWithCamera)

        buttonChoose.setOnClickListener {
            checkAndRequestPermissions(arrayOf(permission.READ_EXTERNAL_STORAGE), ::chooseImageGallery)
        }

        buttonTakeWithCamera.setOnClickListener {
            checkAndRequestPermissions(arrayOf(permission.WRITE_EXTERNAL_STORAGE, permission.CAMERA), ::takePhotoWithCamera)
        }

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
        textView.text = obj.toString()
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
                picture = Picture(data?.data, "")
            }
        }

        if (picture != null && picture?.uri != null) {
            imageView.setImageURI(picture?.uri)
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

}
