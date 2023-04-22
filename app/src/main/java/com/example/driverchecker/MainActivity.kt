package com.example.driverchecker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import com.chaquo.python.PyObject
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class MainActivity : AppCompatActivity() {

    private lateinit var textView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        textView = findViewById(R.id.textView)

        // "context" must be an Activity, Service or Application object from your app.
        if (! Python.isStarted()) {
            Python.start(AndroidPlatform(baseContext));
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
}