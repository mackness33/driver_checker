package com.example.driverchecker.database

import androidx.annotation.WorkerThread
import com.example.driverchecker.machinelearning.data.IImageDetectionOutput
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class PartialRepository(private val partialDao: PartialDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allPartials: Flow<List<PartialEntity>> = partialDao.getAllPartials()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(partial: PartialEntity) {
        partialDao.insert(partial)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(partialResult: IImageDetectionOutput<String>, outputIndex: Int, evaluationId: Long) {
        partialDao.insert(PartialEntity(partialResult, outputIndex, evaluationId))
    }
}
