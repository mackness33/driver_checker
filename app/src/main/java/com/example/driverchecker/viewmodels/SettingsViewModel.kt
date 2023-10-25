package com.example.driverchecker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.driverchecker.database.entity.EvaluationEntity
import com.example.driverchecker.database.ImageDetectionDatabaseRepository
import com.example.driverchecker.utils.PreferencesRepository
import kotlinx.coroutines.launch

class SettingsViewModel(private val repository: PreferencesRepository) : ViewModel() {
    val sharedPreferencesListener = repository.sharedPreferencesListener

    fun commit () = repository.commit()
}