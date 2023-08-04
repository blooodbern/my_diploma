package com.example.diploma.data.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "all_tasks_by_all_time")
data class AllTasksByAllTime (
    @PrimaryKey(autoGenerate = true)
    var id: Int? = null,
    @ColumnInfo(name = "date")
    var date: Long,
    @ColumnInfo(name = "task")
    var task: String,
    @ColumnInfo(name = "description")
    var description: String,
    @ColumnInfo(name = "btn_status")
    var btn_status: Int,
    @ColumnInfo(name = "time")
    var time: Long,
    @ColumnInfo(name = "task_status")
    var task_status: String
    )