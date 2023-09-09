package com.example.driverchecker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "test_table")
data class TestEntity (
    @ColumnInfo(name = "test") val test: String,
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
)
