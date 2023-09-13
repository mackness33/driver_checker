package com.example.driverchecker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.driverchecker.database.EvaluationEntity
import com.example.driverchecker.database.EvaluationRepository
import com.example.driverchecker.database.PartialEntity
import kotlinx.coroutines.launch

class DisplayResultViewModel(private val repository: EvaluationRepository) : ViewModel() {
    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allPartials: LiveData<List<PartialEntity>> = repository.allPartials.asLiveData()

    var metricsPerGroup: Map<String, Triple<Int, Int, Int>?> = emptyMap()
        private set

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun update(name: String) = viewModelScope.launch {
//        repository.(evaluation)
    }
}