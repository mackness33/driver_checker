package com.example.driverchecker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.driverchecker.data.TestEntity
import com.example.driverchecker.data.TestRepo
import kotlinx.coroutines.launch

class LogViewModel(private val repository: TestRepo) : ViewModel() {
    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allWords: LiveData<List<TestEntity>> = repository.allWords.asLiveData()



    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(test: TestEntity) = viewModelScope.launch {
        repository.insert(test)
    }
}