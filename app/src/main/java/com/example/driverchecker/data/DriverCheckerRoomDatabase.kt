package com.example.driverchecker.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

// Annotates class to be a Room Database with a table (entity) of the Word class
@Database(entities = [TestEntity::class], version = 1, exportSchema = false)
abstract class DriverCheckerRoomDatabase : RoomDatabase() {

    abstract fun testDao(): TestDao

    private class DriverCheckerRoomDatabaseCallback(
        private val scope: CoroutineScope
    ) : Callback() {

        override fun onCreate(db: SupportSQLiteDatabase) {
            super.onCreate(db)
            INSTANCE?.let { database ->
                scope.launch {
                    val wordDao = database.testDao()

                    // Delete all content here.
                    wordDao.deleteAll()

                    // Add sample words.
                    var word = TestEntity("Hello")
                    wordDao.insert(word)
                    word = TestEntity("World!")
                    wordDao.insert(word)

                    // TODO: Add your own words!
                    word = TestEntity("TODO!")
                    wordDao.insert(word)
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
                 .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
