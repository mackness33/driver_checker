package com.example.driverchecker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.driverchecker.database.EvaluationEntity
import com.example.driverchecker.database.EvaluationRepository
import kotlinx.coroutines.launch

class LogViewModel(private val repository: EvaluationRepository) : ViewModel() {
    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allEvaluations: LiveData<List<EvaluationEntity>> = repository.allEvaluations.asLiveData()



    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(evaluation: EvaluationEntity) = viewModelScope.launch {
        repository.insert(evaluation)
    }

    fun delete(id: Long) = viewModelScope.launch {
        repository.delete(id)
    }
}