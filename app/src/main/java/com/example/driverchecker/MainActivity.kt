package com.example.driverchecker

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.widget.Toolbar
import androidx.core.view.get
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import androidx.navigation.ui.NavigationUI.navigateUp
import com.example.driverchecker.data.CameraViewModel
import com.example.driverchecker.data.CameraViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(){

    private lateinit var mAppBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var toolbar: Toolbar
    private val model: CameraViewModel by viewModels() {
        CameraViewModelFactory((application as DriverChecker).repository)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main);
        model
        /* TOOLBAR */
        toolbar = findViewById(R.id.material_toolbar)
        setSupportActionBar(toolbar)

        /* NAVIGATION BAR */
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_container_view) as NavHostFragment
        navController = navHostFragment.navController
        mAppBarConfiguration = AppBarConfiguration(navController.graph)
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration)
        NavigationUI.setupWithNavController(toolbar, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.logFragment -> {
//                    toolbar.menu[R.id.aboutFragment].isVisible = true
//                    toolbar.menu[R.id.settingsFragment].isVisible = false
//                    toolbar.menu[R.id.infoFragment].isVisible = false
//                    toolbar.menu[R.id.logFragment].isVisible = false
                    changeVisibilityOfNonAction(info = false, about = true, settings = false)
                    changeVisibilityOfAction(log = false)
                }
                R.id.cameraFragment -> {
//                    toolbar.menu[R.id.aboutFragment].isVisible = true
//                    toolbar.menu[R.id.settingsFragment].isVisible = true
//                    toolbar.menu[R.id.infoFragment].isVisible = true
//                    toolbar.menu[R.id.logFragment].isVisible = true
                    changeVisibilityOfNonAction(info = true, about = true, settings = true)
                    changeVisibilityOfAction(log = true)
                }
                R.id.resultFragment -> {
//                    toolbar.menu[R.id.aboutFragment].isVisible = true
//                    toolbar.menu[R.id.settingsFragment].isVisible = false
//                    toolbar.menu[R.id.infoFragment].isVisible = false
//                    toolbar.menu[R.id.logFragment].isVisible = true
                    changeVisibilityOfNonAction(info = false, about = true, settings = false)
                    changeVisibilityOfAction(log = true)
                }
                else -> {
//                    toolbar.menu[R.id.aboutFragment].isVisible = false
//                    toolbar.menu[R.id.settingsFragment].isVisible = false
//                    toolbar.menu[R.id.infoFragment].isVisible = false
//                    toolbar.menu[R.id.logFragment].isVisible = false
                    changeVisibilityOfNonAction(info = false, about = false, settings = false)
                    changeVisibilityOfAction(log = false)
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    fun changeVisibilityOfNonAction (info: Boolean = true, about: Boolean = true, settings: Boolean = true) {
        (toolbar.menu as MenuBuilder).nonActionItems.forEach { menuItem ->
            when (menuItem.title) {
                "Settings" -> {
                    menuItem.isEnabled = settings
//                    menuItem.isVisible = settings
                }
                "Info" -> {
                    menuItem.isEnabled = info
//                    menuItem.isVisible = info
                }
                "About" -> {
                    menuItem.isEnabled = about
//                    menuItem.isVisible = about
                }
            }
        }
    }

    @SuppressLint("RestrictedApi")
    fun changeVisibilityOfAction (log: Boolean = true) {
        (toolbar.menu as MenuBuilder).actionItems.forEach { menuItem ->
            when (menuItem.title) {
                "Log" -> {
                    menuItem.isEnabled = log
//                    menuItem.isVisible = log
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return item.onNavDestinationSelected(navController) || super.onOptionsItemSelected(item)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_bottom_nav, menu)
        return super.onCreateOptionsMenu(menu)
    }


    override fun onSupportNavigateUp(): Boolean {
        return navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp()
    }
}