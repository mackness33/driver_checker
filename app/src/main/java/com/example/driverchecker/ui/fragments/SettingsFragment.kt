package com.example.driverchecker.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.preference.PreferenceFragmentCompat
import com.example.driverchecker.DriverChecker
import com.example.driverchecker.R
import com.example.driverchecker.machinelearning.data.SettingsState
import com.example.driverchecker.machinelearning.data.SettingsStateInterface
import com.example.driverchecker.utils.PreferencesRepository
import com.example.driverchecker.viewmodels.*
import kotlin.properties.Delegates

class SettingsFragment : PreferenceFragmentCompat() {
    /*
    * Map the categories on the preferences repositories to get the the differences
    * for each
    * */
    private val settingsViewModel: SettingsViewModel by viewModels {
        SettingsViewModelFactory((requireActivity().application as DriverChecker).preferencesRepository)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }

    override fun onResume() {
        super.onResume()
        preferenceManager.sharedPreferences?.registerOnSharedPreferenceChangeListener(
            settingsViewModel.sharedPreferencesListener
        )
    }

    override fun onPause() {
        super.onPause()
        preferenceManager.sharedPreferences?.unregisterOnSharedPreferenceChangeListener(
            settingsViewModel.sharedPreferencesListener
        )
        settingsViewModel.commit()
    }
}