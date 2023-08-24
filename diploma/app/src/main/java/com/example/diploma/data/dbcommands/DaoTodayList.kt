package com.example.diploma.data.dbcommands

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.diploma.data.tables.TodayList
import com.example.diploma.domain.ListItem

@Dao
interface DaoTodayList {
    //@Insert(onConflict = OnConflictStrategy.REPLACE)
    @Insert
    fun insertItem(todayList: TodayList)
    @Update
    fun replaceItem(todayList: TodayList)
    @Query("SELECT * FROM today_list")
    fun getAllItems(): List<TodayList>
    @Query("SELECT COUNT(*) FROM today_list WHERE task = :userTask")
    fun getItemByTask(userTask:String):Int
    @Query("DELETE FROM today_list WHERE id = :itemID")
    fun deleteItem(itemID:Int)
    @Query("SELECT COUNT(*) FROM today_list WHERE id = :itemID")
    fun getItemCnt(itemID:Int):Int
    @Query("SELECT COUNT(*) FROM today_list")
    fun amountOfItems():Int
}