package com.example.diploma.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.diploma.data.tables.AllTasksByAllTime

@Database(
    entities = [AllTasksByAllTime::class],
    version = 1
)
abstract class DatabaseMain: RoomDatabase() {
}