package com.example.driverchecker.ui.fragments

import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import androidx.preference.MultiSelectListPreference
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.PreferenceManager
import com.example.driverchecker.DriverChecker
import com.example.driverchecker.MainActivity
import com.example.driverchecker.R
import com.example.driverchecker.viewmodels.*

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

        val sharedPrefs = preferenceManager.sharedPreferences
        Log.d("SHARED PREFS", sharedPrefs?.all.toString())

        findPreference<MultiSelectListPreference?>("windows_types")
            ?.setOnPreferenceChangeListener { preference, newValue ->
                Log.d("TYPES", "preference: $preference, newValue: ${newValue}")
                true
            }
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