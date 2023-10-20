package com.example.driverchecker

import android.content.Context
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.*
import androidx.navigation.ui.NavigationUI.navigateUp
import com.example.driverchecker.machinelearning.data.LiveEvaluationState
import com.example.driverchecker.utils.PreferencesRepository
import com.example.driverchecker.viewmodels.CameraViewModel
import com.example.driverchecker.viewmodels.CameraViewModelFactory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob

class MainActivity : AppCompatActivity(){

//    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = Some.IMAGE_DETECTION_PREFERENCES_NAME)
//    private val activityScope = CoroutineScope(SupervisorJob())
//    val preferencesRepository by lazy { PreferencesRepository((application as DriverChecker).dataStore, activityScope) }


    companion object Some {
        const val IMAGE_DETECTION_PREFERENCES_NAME = "image_detection_settings"
    }

    private lateinit var mAppBarConfiguration: AppBarConfiguration
    private lateinit var navController: NavController
    private lateinit var toolbar: Toolbar
    private val model: CameraViewModel by viewModels() {
        CameraViewModelFactory(
            (application as DriverChecker).repository,
            (application as DriverChecker).imageDetectionDatabaseRepository,
            (application as DriverChecker).preferencesRepository
        )
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
        menuItemsVisibility = listOf(R.id.logFragment, R.id.aboutFragment, R.id.settingsDialog, R.id.infoFragment, R.id.open_settings_fragment).associateWith { true }
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

        model.currentState.observe(this) { state ->
            if (state == LiveEvaluationState.Ready(true)) {
                changeVisibilityOfMenu(info = true, about = true, settings = true, log = true)
            } else {
                changeVisibilityOfMenu(info = true, about = true, settings = false, log = true)
            }
            invalidateOptionsMenu()
        }
    }

    private fun changeVisibilityOfMenu (log: Boolean, info: Boolean, about: Boolean, settings: Boolean) {
        menuItemsVisibility = menuItemsVisibility.mapValues { menuEntry ->
            when (menuEntry.key) {
                R.id.logFragment -> log
                R.id.aboutFragment -> about
                R.id.open_settings_fragment -> settings
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