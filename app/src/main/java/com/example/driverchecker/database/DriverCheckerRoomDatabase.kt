package com.example.driverchecker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(
    entities = [
        EvaluationEntity::class,
        PartialEntity::class,
        ItemEntity::class,
        MetricsPerEvaluationEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class DriverCheckerRoomDatabase : RoomDatabase() {

    abstract fun evaluationDao(): EvaluationDao
    abstract fun partialDao(): PartialDao
    abstract fun itemDao(): ItemDao
    abstract fun metricsDao (): MetricsPerEvaluationDao

    private class DriverCheckerRoomDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val evaluationDao = database.evaluationDao()
//                    val partialDao = database.partialDao()
//                    val itemDao = database.itemDao()

                    // Delete all content here.
                    evaluationDao.deleteAll()
//                    partialDao.deleteAll()
//                    itemDao.deleteAll()

                    // Add sample words.
                    var evaluation = EvaluationEntity(0.03f, "first", "driver")
                    evaluationDao.insert(evaluation)

//                    var partial = PartialEntity(0.20f, 1, evaluation.id)
//                    partialDao.insert(partial)
//
//             aaa       var item = ItemEntity(0.21f, "right-belt", partial.id)
//                    itemDao.insert(item)
//                    item = ItemEntity(0.22f, "right-window", partial.id)
//                    itemDao.insert(item)
//                    item = ItemEntity(0.24f, "left-window", partial.id)
//                    itemDao.insert(item)
//                    item = ItemEntity(0.27f, "left-window", partial.id)
//                    itemDao.insert(item)
//
//                    partial = PartialEntity(0.40f, 2, evaluation.id)
//                    partialDao.insert(partial)
//                    item = ItemEntity(0.44f, "left-window", partial.id)
//                    itemDao.insert(item)
//                    item = ItemEntity(0.47f, "left-belt", partial.id)
//                    itemDao.insert(item)
//
//                    partial = PartialEntity(0.60f, 3, evaluation.id)
//                    partialDao.insert(partial)
//                    item = ItemEntity(0.64f, "left-window", partial.id)
//                    itemDao.insert(item)



                    evaluation = EvaluationEntity(0.06f, "second", "passenger")
                    evaluationDao.insert(evaluation)

//                    partial = PartialEntity(0.70f, 1, evaluation.id)
//                    partialDao.insert(partial)
//
//                    item = ItemEntity(0.71f, "right-belt", partial.id)
//                    itemDao.insert(item)
//                    item = ItemEntity(0.72f, "right-window", partial.id)
//                    itemDao.insert(item)
//                    item = ItemEntity(0.74f, "left-window", partial.id)
//                    itemDao.insert(item)
//                    item = ItemEntity(0.77f, "left-window", partial.id)
//                    itemDao.insert(item)
//
//                    partial = PartialEntity(0.80f, 2, evaluation.id)
//                    partialDao.insert(partial)
//                    item = ItemEntity(0.84f, "left-window", partial.id)
//                    itemDao.insert(item)
//                    item = ItemEntity(0.87f, "left-belt", partial.id)
//                    itemDao.insert(item)
//
//                    partial = PartialEntity(0.90f, 3, evaluation.id)
//                    partialDao.insert(partial)
//                    item = ItemEntity(0.94f, "right-window", partial.id)
//                    itemDao.insert(item)
                }
            }
        }
    }


    companion object {
        // Singleton prevents multiple instances of database opening at the
        // same time.
        @Volatile
        private var INSTANCE: DriverCheckerRoomDatabase? = null

        fun getDatabase(context: Context, scope: CoroutineScope): DriverCheckerRoomDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    DriverCheckerRoomDatabase::class.java,
                    "test_database"
                )
                 .addCallback(DriverCheckerRoomDatabaseCallback(scope))
                 .fallbackToDestructiveMigration()
                 .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
