package com.example.driverchecker.machinelearning.general

import com.example.driverchecker.machinelearning.general.local.MLLocalModel
import com.example.driverchecker.machinelearning.general.local.MLLocalRepository
import com.example.driverchecker.machinelearning.general.remote.MLRemoteModel
import com.example.driverchecker.machinelearning.general.remote.MLRemoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asFlow

abstract class MLRepository<Data, Result> () : MLRepositoryInterface<Data, Result> {
    protected val isOnline: Boolean = false
    protected var local: MLLocalRepository<Data, Result>? = null
    protected var remote: MLRemoteRepository<Data, Result>? = null

    override suspend fun instantClassification(input: Data): Result? {
        return if (isOnline) remote?.instantClassification(input) else local?.instantClassification(input)
    }

    override suspend fun continuousClassification(input: List<Data>): Result? {
        return if (isOnline) remote?.continuousClassification(input) else local?.continuousClassification(input.asFlow())
    }

    override suspend fun continuousClassification(input: Flow<Data>): Result? {
        return if (isOnline) remote?.continuousClassification(input) else local?.continuousClassification(input)
    }


    fun updateRemoteModel (url: String) {
        remote?.updateRemoteUrl(url)
    }

    fun updateLocalModel (url: String) {
        local?.updateLocalModel(url)
    }
}