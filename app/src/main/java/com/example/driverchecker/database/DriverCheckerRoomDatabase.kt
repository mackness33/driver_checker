package com.example.driverchecker.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.driverchecker.database.dao.EvaluationDao
import com.example.driverchecker.database.dao.ItemDao
import com.example.driverchecker.database.dao.MetricsPerEvaluationDao
import com.example.driverchecker.database.dao.PartialDao
import com.example.driverchecker.database.entity.EvaluationEntity
import com.example.driverchecker.database.entity.ItemEntity
import com.example.driverchecker.database.entity.MetricsPerEvaluationEntity
import com.example.driverchecker.database.entity.PartialEntity
import kotlinx.coroutines.CoroutineScope

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(
    entities = [
        EvaluationEntity::class,
        PartialEntity::class,
        ItemEntity::class,
        MetricsPerEvaluationEntity::class
    ],
    version = 8,
    exportSchema = false
)
abstract class DriverCheckerRoomDatabase : RoomDatabase() {

    abstract fun evaluationDao(): EvaluationDao
    abstract fun partialDao(): PartialDao
    abstract fun itemDao(): ItemDao
    abstract fun metricsDao (): MetricsPerEvaluationDao

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
                 .fallbackToDestructiveMigration()
                 .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
