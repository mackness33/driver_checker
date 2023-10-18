package com.example.driverchecker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.driverchecker.database.ImageDetectionDatabaseRepository
import com.example.driverchecker.machinelearning.repositories.ImageDetectionFactoryRepository
import com.example.driverchecker.utils.PreferencesRepository

class CameraViewModelFactory(
    private val modelRepository: ImageDetectionFactoryRepository,
    private val databaseRepository: ImageDetectionDatabaseRepository,
    private val preferencesRepository: PreferencesRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CameraViewModel(modelRepository, databaseRepository, preferencesRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class $modelClass")
    }
}

class LogViewModelFactory(private val databaseRepository: ImageDetectionDatabaseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LogViewModel(databaseRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class $modelClass")
    }
}

class DisplayResultViewModelFactory(private val repository: ImageDetectionDatabaseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DisplayResultViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DisplayResultViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class $modelClass")
    }
}


class SettingsViewModelFactory(private val repository: PreferencesRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SettingsViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return SettingsViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class $modelClass")
    }
}


class StaticPhotoViewModelFactory(private val repository: ImageDetectionDatabaseRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(StaticPhotoViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return StaticPhotoViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class $modelClass")
    }
}