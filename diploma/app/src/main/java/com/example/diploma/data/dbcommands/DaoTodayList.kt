package com.example.diploma.data.dbcommands

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.diploma.data.tables.TodayList

@Dao
interface DaoTodayList {
    @Insert
    fun insertItem(todayList: TodayList)
    @Query("SELECT * FROM today_list")
    fun getAllTasks(): List<TodayList>
    @Query("SELECT id FROM today_list WHERE id = (SELECT MAX(id) FROM today_list)")
    fun getMaxID(): Int
    @Query("SELECT id FROM today_list WHERE id = (SELECT MIN(id) FROM today_list WHERE isVisible = 0)")
    fun getMinInvisibleID(): Int
    @Query("UPDATE today_list SET isVisible = 1 WHERE (id = :itemID)")
    fun setItemVisible(itemID:Int)
    @Query("SELECT COUNT(*) FROM today_list WHERE isVisible = 0")
    fun checkInvisibleItems(): Int
}