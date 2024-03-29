package com.example.driverchecker.viewmodels

import androidx.lifecycle.*
import com.example.driverchecker.database.ImageDetectionDatabaseRepository
import com.example.driverchecker.database.entity.ItemEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StaticPhotoViewModel(private val repository: ImageDetectionDatabaseRepository) : ViewModel() {
    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.

    var partialId: Long? = null
        private set

    private val mPartial: MutableLiveData<String> = MutableLiveData(null)
    val partial: LiveData<String>
        get() = mPartial

    private val mItems: MutableLiveData<Pair<List<ItemEntity>, String>> = MutableLiveData(null)
    val items: LiveData<Pair<List<ItemEntity>, String>>
        get() = mItems

    fun initPartialId (id: Long?) = viewModelScope.launch {
        if (id == null || id <= 0) {
            return@launch
        }

        partialId = id

        launch(Dispatchers.IO) {
            val partial = repository.getPartial(id)
            mPartial.postValue(partial.path ?: "")
            val metrics = repository.getAllMetricsOfEvaluationAsMap(partial.evaluationId)
            val items = repository.getAllItemsOfPartial(id)
            mItems.postValue(Pair(items, partial.group))
        }
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun update(name: String) = viewModelScope.launch {
        if (partialId != null && partialId!! > 0)
            repository.updateById(partialId!!, name)
    }
}