package com.example.driverchecker.database

import android.util.Log
import androidx.annotation.WorkerThread
import com.example.driverchecker.machinelearning.data.IImageDetectionFinalResult
import com.example.driverchecker.machinelearning.data.IImageDetectionOutput
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class EvaluationRepository(
    private val evaluationDao: EvaluationDao,
    private val partialDao: PartialDao,
    private val itemDao: ItemDao,
    private val metricsDao: MetricsPerEvaluationDao
) {

    /* GET ALL */
    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allEvaluations: Flow<List<EvaluationEntity>> = evaluationDao.getAllEvaluations()
    val allPartials: Flow<List<PartialEntity>> = partialDao.getAllPartials()


    /* INSERT */
    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertEvaluation(evaluation: EvaluationEntity) {
        evaluationDao.insert(evaluation)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertPartial(partial: PartialEntity) {
        partialDao.insert(partial)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertItem(item: ItemEntity) {
        itemDao.insert(item)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertEvaluation(finalResult: IImageDetectionFinalResult<String>, name: String) : Long {
        val id = evaluationDao.insert(EvaluationEntity(finalResult.confidence, name, finalResult.supergroup))
        Log.d("EvalRepo", "Eval inserted with: $id")
        return id
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAllMetrics(metrics: Map<String, Triple<Int, Int, Int>?>, evalId: Long) : List<Long> {
        val ids = mutableListOf<Long>()

        metrics.forEach { entry ->
            val id = metricsDao.insert(MetricsPerEvaluationEntity(
                entry.toPair(), evalId
            ))

            ids.add(id)
        }

        Log.d("EvalRepo", "Metrics inserted with: $ids")
        return ids
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAllPartials(partialResults: List<IImageDetectionOutput<String>>, evalId: Long) : List<Long> {
        val ids = mutableListOf<Long>()

        partialResults.forEach {
            val id = partialDao.insert(PartialEntity(it, ids.size, evalId))
            ids.add(id)
        }

        Log.d("EvalRepo", "Partials inserted with: $ids")
        return ids
    }

    /* DELETE */
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(evaluationId: Long) {
        evaluationDao.deleteById(evaluationId)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun delete(evaluation: EvaluationEntity) {
        evaluationDao.delete(evaluation)
    }
}
