package com.example.driverchecker.viewmodels

import androidx.lifecycle.*
import com.example.driverchecker.database.EvaluationEntity
import com.example.driverchecker.database.EvaluationRepository
import com.example.driverchecker.database.PartialEntity
import kotlinx.coroutines.launch

class DisplayResultViewModel(private val repository: EvaluationRepository) : ViewModel() {
    // Using LiveData and caching what allWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.

    var evaluationId: Long? = null
        private set

    private val mEvaluation: MutableLiveData<EvaluationEntity> = MutableLiveData(null)
    val evaluation: LiveData<EvaluationEntity>
        get() = mEvaluation

    private val mPartials: MutableLiveData<List<PartialEntity>> = MutableLiveData(null)
    val partials: LiveData<List<PartialEntity>>
        get() = mPartials

    private val mMetricsPerGroup: MutableLiveData<Map<String, Triple<Int, Int, Int>?>> = MutableLiveData(null)
    val metricsPerGroup: LiveData<Map<String, Triple<Int, Int, Int>?>>
        get() = mMetricsPerGroup

    fun initEvaluationId (id: Long?) = viewModelScope.launch {
        if (id == null) {
            return@launch
        }

        evaluationId = id

        launch {
            repository.getAllMetricsOfEvaluationAsMap(id).collect { mMetricsPerGroup.postValue(it) }
        }
        launch {
            repository.getAllPartialsOfEvaluation(id).collect { mPartials.postValue(it) }
        }

        launch {
            repository.getEvaluation(id).collect { mEvaluation.postValue(it) }
        }
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun update(name: String) = viewModelScope.launch {
        if (evaluationId != null && evaluationId!! > 0)
            repository.updateById(evaluationId!!, name)
    }
}