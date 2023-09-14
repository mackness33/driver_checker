package com.example.driverchecker.viewmodels

import androidx.lifecycle.*
import com.example.driverchecker.database.EvaluationEntity
import com.example.driverchecker.database.EvaluationRepository
import com.example.driverchecker.database.ItemEntity
import com.example.driverchecker.database.PartialEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class StaticPhotoViewModel(private val repository: EvaluationRepository) : ViewModel() {
    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.

    var partialId: Long? = null
        private set

    private val mPartial: MutableLiveData<String> = MutableLiveData(null)
    val partial: LiveData<String>
        get() = mPartial

    private val mItems: MutableLiveData<Triple<List<ItemEntity>, Set<String>, String>> = MutableLiveData(null)
    val items: LiveData<Triple<List<ItemEntity>, Set<String>, String>>
        get() = mItems

    fun initPartialId (id: Long?) = viewModelScope.launch {
        if (id == null) {
            return@launch
        }

        partialId = id

        launch(Dispatchers.IO) {
            val partial = repository.getPartial(id)
            mPartial.postValue(partial.path ?: "")
            val metrics = repository.getAllMetricsOfEvaluationAsMap(partial.evaluationId)
            val items = repository.getAllItemsOfPartial(id)
            mItems.postValue(Triple(items, metrics.keys, partial.group))
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