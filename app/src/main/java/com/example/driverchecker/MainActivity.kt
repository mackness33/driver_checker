package com.example.driverchecker

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import com.google.android.material.snackbar.Snackbar


class MainActivity : AppCompatActivity() {

    private lateinit var textView: TextView
    private lateinit var buttonChoose: Button
    private lateinit var imageView: ImageView
    private lateinit var filePhotoUri: Uri

    // companion object
    companion object {
        private val IMAGE_CHOOSE = 1000
        private val PERMISSION_CODE = 1001
        internal val DEBUG_TAG = "MainActivity"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.txtView)
        buttonChoose = findViewById(R.id.btnChoose)
        imageView = findViewById(R.id.imgView)

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


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                // IMAGE_CHOOSE -> if (data?.data !== null) filePhotoUri = data?.data!!
                IMAGE_CHOOSE ->  imageView.setImageURI(data?.data)
            }
        }

        // imageView.setImageURI(filePhotoUri)
    }


    private fun chooseImageGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_CHOOSE)
    }
}