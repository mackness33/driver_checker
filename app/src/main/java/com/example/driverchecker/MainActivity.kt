package com.example.driverchecker

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.NavigationUI.navigateUp
import androidx.navigation.ui.setupWithNavController

class MainActivity : AppCompatActivity() {

    private lateinit var mAppBarConfiguration: AppBarConfiguration
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);

        /* TOOLBAR */
        val toolbar: Toolbar = findViewById(R.id.material_toolbar)
        setSupportActionBar(toolbar)

        /* NAVIGATION BAR */
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        val navController = navHostFragment.navController
//        val navController = findNavController(R.id.fragment_container_view)
        mAppBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration)
        NavigationUI.setupWithNavController(toolbar, navController)
//        toolbar
//            .setupWithNavController(navController, appBarConfiguration)

//        if (savedInstanceState == null) {
//            supportFragmentManager
//                .beginTransaction()
//                .setReorderingAllowed(true)
//                .add(R.id.fragment_container_view, CameraFragment.newInstance(), "Camera")
//                .addToBackStack("Camera")
//                .commit()
//        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }


    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(this, R.id.fragment_container_view)
        return (navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp())
    }
}
