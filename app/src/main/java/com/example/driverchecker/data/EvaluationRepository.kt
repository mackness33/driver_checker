package com.example.driverchecker.data

import androidx.annotation.WorkerThread
import com.example.driverchecker.machinelearning.data.ClassificationFinalResult
import com.example.driverchecker.machinelearning.data.IClassificationFinalResult
import com.example.driverchecker.machinelearning.data.ImageDetectionFinalResult
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class EvaluationRepository(
    private val evaluationDao: EvaluationDao
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
    suspend fun insert(finalResult: IClassificationFinalResult<String>, name: String) {
        evaluationDao.insert(EvaluationEntity(finalResult.confidence, name, finalResult.supergroup))
    }
}
