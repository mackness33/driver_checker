package com.example.driverchecker.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "test_table")
class TestEntity (
    @PrimaryKey(autoGenerate = true) val id: Int,
    @ColumnInfo(name = "test") val test: String
)
