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
import com.example.driverchecker.viewmodels.CameraViewModel
import com.example.driverchecker.viewmodels.CameraViewModelFactory
import com.google.android.material.bottomnavigation.BottomNavigationView

class MainActivity : AppCompatActivity(){

    private lateinit var mAppBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var toolbar: Toolbar
    private val model: CameraViewModel by viewModels() {
        CameraViewModelFactory(
            (application as DriverChecker).repository,
            (application as DriverChecker).evaluationRepository)
    }
    private lateinit var menuItemsVisibility: Map<Int, Boolean>

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

        /* TOOLBAR ACTIONS */
        menuItemsVisibility = listOf(R.id.logFragment, R.id.aboutFragment, R.id.settingsDialog, R.id.infoFragment).associateWith { true }
        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                R.id.logFragment -> {
                    changeVisibilityOfMenu(info = false, about = true, settings = false, log = false)
                }
                R.id.cameraFragment -> {
                    changeVisibilityOfMenu(info = true, about = true, settings = true, log = true)
                }
                R.id.resultFragment -> {
                    changeVisibilityOfMenu(info = false, about = true, settings = false, log = true)
                }
                else -> {
                    changeVisibilityOfMenu(info = false, about = false, settings = false, log = false)
                }
            }
            invalidateOptionsMenu()
        }
    }

    private fun changeVisibilityOfMenu (log: Boolean, info: Boolean, about: Boolean, settings: Boolean) {
        menuItemsVisibility = menuItemsVisibility.mapValues { menuEntry ->
            when (menuEntry.key) {
                R.id.logFragment -> log
                R.id.aboutFragment -> about
                R.id.settingsDialog -> settings
                R.id.infoFragment -> info
                else -> false
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

        menuItemsVisibility.forEach { menuEntry ->
            val item = menu?.findItem(menuEntry.key)
            item?.isEnabled = menuEntry.value
            item?.isVisible = menuEntry.value
        }

        return super.onCreateOptionsMenu(menu)
    }


    override fun onSupportNavigateUp(): Boolean {
        return navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp()
    }
}