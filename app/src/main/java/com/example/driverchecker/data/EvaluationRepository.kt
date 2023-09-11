package com.example.driverchecker.data

import androidx.annotation.WorkerThread
import com.example.driverchecker.machinelearning.data.IClassificationFinalResult
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class EvaluationRepository(
    private val evaluationDao: EvaluationDao,
    private val partialDao: PartialDao,
    private val itemDao: ItemDao
) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allEvaluations: Flow<List<EvaluationEntity>> = evaluationDao.getEvaluations()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(evaluation: EvaluationEntity) {
        evaluationDao.insert(evaluation)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(partial: PartialEntity) {
        partialDao.insert(partial)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(item: ItemEntity) {
        itemDao.insert(item)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(finalResult: IClassificationFinalResult<String>, name: String) {
        evaluationDao.insert(EvaluationEntity(finalResult.confidence, name, finalResult.supergroup))
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(evaluationId: Int) {
        evaluationDao.deleteById(evaluationId)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(evaluation: EvaluationEntity) {
        evaluationDao.delete(evaluation)
    }
}
