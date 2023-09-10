package com.example.driverchecker.data

import androidx.annotation.WorkerThread
import com.example.driverchecker.machinelearning.data.IImageDetectionItem
import kotlinx.coroutines.flow.Flow

// Declares the DAO as a private property in the constructor. Pass in the DAO
// instead of the whole database, because you only need access to the DAO
class ItemRepository(private val itemDao: ItemDao) {

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allItems: Flow<List<ItemEntity>> = itemDao.getItems()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(item: ItemEntity) {
        itemDao.insert(item)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insert(itemResult: IImageDetectionItem<String>, partialId: Int) {
        itemDao.insert(ItemEntity(itemResult.confidence, itemResult.classification.name, partialId))
    }
}
