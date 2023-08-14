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
    @Query("UPDATE today_list SET isVisible = 0 WHERE (id = :itemID)")
    fun setItemInvisible(itemID:Int)
    @Query("SELECT COUNT(*) FROM today_list WHERE isVisible = 0")
    fun checkInvisibleItems(): Int
    @Query("SELECT COUNT(*) FROM today_list WHERE isVisible = 1")
    fun checkVisibleItems(): Int
    @Query("UPDATE today_list SET isStop = 1 WHERE (id = :itemID)")
    fun setItemStop(itemID:Int)
    @Query("UPDATE today_list SET isStop = 0 WHERE (id = :itemID)")
    fun setItemNotStop(itemID:Int)
    @Query("UPDATE today_list SET isPlaying = 0 WHERE (id = :itemID)")
    fun setItemNotPlaying(itemID:Int)
    @Query("SELECT COUNT(*) FROM today_list WHERE (id = :itemID AND isStop = 1)")
    fun checkItemStop(itemID:Int): Int
    @Query("UPDATE today_list SET lastChanged = 1 WHERE (id = :itemID)")
    fun setItemLastChanged(itemID:Int)
    @Query("UPDATE today_list SET lastChanged = 0 WHERE (lastChanged = 1)")
    fun unsetItemLastChanged()
    @Query("SELECT id FROM today_list WHERE lastChanged = 1")
    fun getLastChangedItemId():Int
    @Query("UPDATE today_list SET status = :status WHERE lastChanged = 1")
    fun setItemStatus(status:String)
    @Query("UPDATE today_list SET task = :task WHERE id = :id")
    fun setItemName(task:String, id:Int)
    @Query("UPDATE today_list SET description = :description WHERE id = :id")
    fun setItemDesc(description:String, id:Int)
    @Query("UPDATE today_list SET time = :time WHERE id = :id")
    fun setItemTime(time:Long, id:Int)
}