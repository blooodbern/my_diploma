package com.example.diploma.data.tables

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "today_list")
data class TodayList(
    @PrimaryKey
    var id: Int,
    @ColumnInfo(name = "task")
    var task: String,
    @ColumnInfo(name = "description")
    var description: String,
    @ColumnInfo(name = "time")
    var time: Long,
    @ColumnInfo(name = "status")
    var status: Int,
    @ColumnInfo(name = "isPlaying")
    var isPlaying: Boolean,
    @ColumnInfo(name = "isStop")
    var isStop: Boolean
)
