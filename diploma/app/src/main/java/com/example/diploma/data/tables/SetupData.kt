package com.example.diploma.data.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "setup_data")
data class SetupData(
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "setting_name")
    var setting_name: String,
    @ColumnInfo(name = "setting_value")
    var setting_value: Int
)
