package com.example.driverchecker.machinelearning.general

import com.example.driverchecker.machinelearning.general.local.MLLocalModel
import com.example.driverchecker.machinelearning.general.local.MLLocalRepository
import com.example.driverchecker.machinelearning.general.remote.MLRemoteModel
import com.example.driverchecker.machinelearning.general.remote.MLRemoteRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

abstract class MLRepository<Data, Result> () : MLRepositoryInterface<Data, Result> {
    protected val isOnline: Boolean = false
    // IDEA: can be transformed into a set of internal repositories
    protected var local: MLLocalRepository<Data, Result>? = null
    protected var remote: MLRemoteRepository<Data, Result>? = null

    override suspend fun instantClassification(input: Data): Result? {
        return if (isOnline) remote?.instantClassification(input) else local?.instantClassification(input)
    }

    override suspend fun continuousClassification(input: List<Data>): Result? {
        return if (isOnline) remote?.continuousClassification(input) else null
    }

    override suspend fun continuousClassification(input: Flow<Data>, scope: CoroutineScope): Result? {
        return if (isOnline) remote?.continuousClassification(input, scope) else local?.continuousClassification(input, scope)
    }

    override suspend fun onStartLiveClassification(input: SharedFlow<Data>, scope: CoroutineScope) {
        if (isOnline) remote?.onStartLiveClassification(input, scope) else local?.onStartLiveClassification(input, scope)
    }

    override suspend fun onStopLiveClassification() {
        if (isOnline) remote?.onStopLiveClassification() else local?.onStopLiveClassification()
    }

    fun updateRemoteModel (url: String) {
        remote?.updateRemoteUrl(url)
    }

    fun updateLocalModel (url: String) {
        local?.updateLocalModel(url)
    }
}