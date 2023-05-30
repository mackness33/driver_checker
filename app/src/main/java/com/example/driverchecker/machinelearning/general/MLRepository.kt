package com.example.driverchecker.machinelearning.general

import com.example.driverchecker.machinelearning.general.local.MLLocalModel
import com.example.driverchecker.machinelearning.general.local.MLLocalRepository
import com.example.driverchecker.machinelearning.general.remote.MLRemoteModel
import com.example.driverchecker.machinelearning.general.remote.MLRemoteRepository

abstract class MLRepository<Data, Result> () : MLRepositoryInterface<Data, Result> {
    protected val isOnline: Boolean = false
    protected var local: MLLocalRepository<Data, Result>? = null
    protected var remote: MLRemoteRepository<Data, Result>? = null

    override suspend fun instantClassification(input: Data): Result? {
        return if (isOnline) remote?.instantClassification(input) else local?.instantClassification(input)
    }

    override suspend fun continuousClassification(input: List<Data>): Result? {
//        TODO("Not yet implemented")

        return null
    }

    protected fun initializeRepos (localRepo: MLLocalRepository<Data, Result>, remoteRepo: MLRemoteRepository<Data, Result>) {
        local = localRepo
        remote = remoteRepo
    }

    protected fun initializeLocalRepo (localRepo: MLLocalRepository<Data, Result>) {
        local = localRepo
    }

    protected fun initializeRemoteRepo (remoteRepo: MLRemoteRepository<Data, Result>) {
        remote = remoteRepo
    }

    fun updateRemoteModel (url: String) {
        remote?.updateRemoteUrl(url)
    }

    fun updateLocalModel (url: String) {
        local?.updateLocalModel(url)
    }
}