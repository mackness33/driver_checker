package com.example.driverchecker.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.driverchecker.data.EvaluationRepository
import com.example.driverchecker.data.TestRepo
import com.example.driverchecker.machinelearning.repositories.ImageDetectionFactoryRepository

class CameraViewModelFactory(
    private val imageDetectionRepository: ImageDetectionFactoryRepository,
    private val evaluationRepository: EvaluationRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CameraViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return CameraViewModel(imageDetectionRepository, evaluationRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class $modelClass")
    }
}

class LogViewModelFactory(private val repository: EvaluationRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(LogViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return LogViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class $modelClass")
    }
}