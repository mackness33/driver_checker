package com.example.driverchecker.database

import android.util.Log
import androidx.annotation.WorkerThread
import com.example.driverchecker.database.dao.*
import com.example.driverchecker.database.entity.*
import com.example.driverchecker.machinelearning.data.*
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class ImageDetectionDatabaseRepository(
    private val evaluationDao: EvaluationDao,
    private val partialDao: PartialDao,
    private val itemDao: ItemDao,
    private val metricsDao: MetricsPerEvaluationDao,
    private val windowInformationDao: WindowInformationDao,
    private val groupMetricsDao: GroupMetricsDao
) {

    /* GET ALL */
    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allEvaluations: Flow<List<EvaluationEntity>> = evaluationDao.getAllEvaluations()
    val allPartials: Flow<List<PartialEntity>> = partialDao.getAllPartials()

    /* QUERY */
    fun getAllMetricsOfEvaluation (evaluationId: Long) : List<MetricsPerEvaluationEntity> {
        return metricsDao.getMetricsPerEvaluation(evaluationId)
    }

    fun getAllMetricsOfEvaluationAsMap (evaluationId: Long) : Map<String, Triple<Int, Int, Int>> {
        val metricMap: MutableMap<String, Triple<Int, Int, Int>> = mutableMapOf()
        metricsDao.getMetricsPerEvaluation(evaluationId).forEach { metric ->
            metricMap[metric.group] = Triple(metric.totImages, metric.totClasses, metric.totObjects)
        }

        return metricMap
    }

    fun getAllInformationOfEvaluationAsMap (evaluationId: Long) : Map<IWindowBasicData, IGroupMetrics<String>> {
        val infoMap: MutableMap<IWindowBasicData, IGroupMetrics<String>> = mutableMapOf()
        windowInformationDao.getWindowInformationByEvaluation(evaluationId).forEach { window ->
            val groupMetricPerWindowMap: MutableMap<String, Triple<Int, Int, Int>> = mutableMapOf()
            val groupMetric = groupMetricsDao.getGroupMetricsByWindowMetrics(window.id)
            infoMap.putIfAbsent(
                WindowBasicData(window.metrics, window.settings),
                GroupMetrics(groupMetric.associate {
                    it.group to Triple(it.totalImages, it.totalClasses, it.totalObjects)
                }.toList())
            )
        }

        return infoMap
    }

    fun getAllPartialsOfEvaluation (evaluationId: Long) : Flow<List<PartialEntity>> {
        return partialDao.getPartialsPerEvaluation(evaluationId)
    }

    fun getAllItemsOfPartial (partialId: Long) : List<ItemEntity> {
        return itemDao.getItemsPerPartial(partialId)
    }

    fun getEvaluation (evaluationId: Long) : EvaluationEntity = evaluationDao.getEvaluation(evaluationId)

    fun getPartial (partialId: Long) : PartialEntity = partialDao.getPartial(partialId)

    /* INSERT */
    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertFinalResult(evaluation: EvaluationEntity) {
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
    suspend fun insertFinalResult(
        finalResult: IClassificationFinalResult<String>,
        name: String,
        modelSettings: SettingsState.ModelSettings,
    ) : Long {
        val id = evaluationDao.insert(EvaluationEntity(finalResult, name, modelSettings))

        val ids = mutableListOf<Long>()

        finalResult.metrics?.data?.forEach { window ->
            val metricId = windowInformationDao.insert(WindowInformationEntity(window.key, id))

            val groupIds = mutableListOf<Long>()

            window.value?.groupMetrics?.forEach { groupMetric ->
                val groupId = groupMetricsDao.insert(GroupMetricsEntity(groupMetric.toPair(), metricId))

                groupIds.add(groupId)
            }

            ids.add(metricId)

            Log.d("EvalRepo", "Total GroupMetrics inserted: $groupIds.size")
        }

        Log.d("EvalRepo", "Total Metrics inserted: ${ids.size}")
        Log.d("EvalRepo", "Eval inserted with: $id")
        return id
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAllOldMetrics(metrics: Map<String, Triple<Int, Int, Int>?>, evalId: Long) : List<Long> {
        val ids = mutableListOf<Long>()

        metrics.forEach { entry ->
            val id = metricsDao.insert(
                MetricsPerEvaluationEntity(
                entry.toPair(), evalId
            )
            )

            ids.add(id)
        }

        Log.d("EvalRepo", "Metrics inserted with: $ids")
        return ids
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAllWindowMetrics(metrics: Map<IWindowBasicData, IGroupMetrics<String>?>, evalId: Long) : List<Long> {
        val ids = mutableListOf<Long>()

        metrics.forEach { window ->
            val id = windowInformationDao.insert(WindowInformationEntity(window.key, evalId))

            val groupIds = mutableListOf<Long>()

            window.value?.groupMetrics?.forEach { groupMetric ->
                val groupId = groupMetricsDao.insert(GroupMetricsEntity(groupMetric.toPair(), id))

                groupIds.add(groupId)
            }

            ids.add(id)

            Log.d("EvalRepo", "GroupMetrics inserted with: $groupIds")
        }

//        metrics.forEach { entry ->
//            val id = metricsDao.insert(
//                MetricsPerEvaluationEntity(
//                    entry.toPair(), evalId
//                )
//            )
//
//            ids.add(id)
//        }

        Log.d("EvalRepo", "Metrics inserted with: $ids")
        return ids
    }



    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAllPartials(partialResults: List<IImageDetectionOutput<String>>, evalId: Long, paths: List<String?>?) : List<Long> {
        val ids = mutableListOf<Long>()

        for (index in partialResults.indices) {
            val id = partialDao.insert(PartialEntity(partialResults[index], ids.size, evalId, paths?.get(index)))
            ids.add(id)
        }

        Log.d("EvalRepo", "Partials inserted with: $ids")
        return ids
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAllPartialsAndItems(partialResults: List<IImageDetectionOutput<String>>, evalId: Long, paths: List<String?>?) : List<Long> {
        val ids = mutableListOf<Long>()

        for (index in partialResults.indices) {
            val id = partialDao.insert(PartialEntity(partialResults[index], ids.size, evalId, paths?.get(index)))
            val itemsIds = mutableListOf<Long>()

            partialResults[index].items.forEach { itemsIds.add(itemDao.insert(ItemEntity(it, id))) }
            Log.d("EvalRepo", "Items inserted with: $itemsIds")

            ids.add(id)
        }

        Log.d("EvalRepo", "Partials inserted with: $ids")
        return ids
    }

    /* UPDATE */
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun updateById (evaluationId: Long, name: String) {
        evaluationDao.updateById(evaluationId, name)
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
