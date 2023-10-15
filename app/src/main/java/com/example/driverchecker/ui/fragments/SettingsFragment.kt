package com.example.driverchecker.ui.fragments

import android.os.Bundle
import android.util.Log
import androidx.preference.PreferenceFragmentCompat
import com.example.driverchecker.R

class MySettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)

//        findPreference("notifications")
//            ?.setOnPreferenceChangeListener { _, newValue ->
//                Log.d("Preferences", "Notifications enabled: $newValue")
//                true // Return true if the event is handled.
//            }
//
//        findPreference("feedback")
//            ?.setOnPreferenceClickListener {
//                Log.d("Preferences", "Feedback was clicked")
//                true // Return true if the click is handled.
//            }
    }
}
