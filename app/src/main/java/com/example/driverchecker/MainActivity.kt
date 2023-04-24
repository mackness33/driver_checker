package com.example.driverchecker

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.FileProvider
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import java.io.File

class MainActivity : AppCompatActivity() {

    private lateinit var filePhoto: File
    private lateinit var textView: TextView
    private lateinit var buttonView: Button
    private lateinit var buttonChoose: Button
    private lateinit var imageView: ImageView
    private val FILENAME = "driver_checker.jpg"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.txtView)
        buttonView = findViewById(R.id.btnView)
        buttonChoose = findViewById(R.id.btnChoose)

        buttonView.setOnClickListener {
            val takePhotoIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            filePhoto = this.getPhotoFile(FILENAME)
            val providerFile = FileProvider.getUriForFile(this,"com.example.driver_checker.file_provider", filePhoto)
            takePhotoIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerFile)
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

        imageView = findViewById(R.id.imgView)
    }

    // companion object
    companion object {
        private val IMAGE_CHOOSE = 1000
        private val PERMISSION_CODE = 1001
    }

    private fun getPhotoFile(fileName: String): File {
        val directoryStorage = getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        return File.createTempFile(fileName, "driver_checker.jpg", directoryStorage)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        /** val REQUEST_CODE = 13
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
             val takenPhoto = BitmapFactory.decodeFile(filePhoto.absolutePath)
            imageView.setImageBitmap(takenPhoto)
        } else if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            imageView.setImageURI(data?.data)
        } else {
            super.onActivityResult(requestCode, resultCode, data)
        }
        **/
        val REQUEST_CODE = 13
        if(requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK){
            imageView.setImageURI(data?.data)
        }
    }
    // private const val REQUEST_CODE = 13
    //you can type a random number, this is not important

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
                }else{
                    Toast.makeText(this,"Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}