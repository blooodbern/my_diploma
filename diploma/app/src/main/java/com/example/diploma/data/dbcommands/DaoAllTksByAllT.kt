package com.example.diploma.data.dbcommands

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.diploma.data.tables.AllTasksByAllTime
import kotlinx.coroutines.flow.Flow

@Dao
interface DaoAllTksByAllT {
    @Insert
    fun insertItem(allTasks: AllTasksByAllTime)
    @Query("SELECT COUNT(*) FROM all_tasks_by_all_time WHERE (task = :userTask AND date = :userDate)")
    fun checkIfTaskExists(userTask: String, userDate: Long): Int
    @Query("SELECT * FROM all_tasks_by_all_time WHERE date = :userDate")
    fun getAllTasksByDate(userDate: Long): Flow<List<AllTasksByAllTime>>
    @Query("SELECT * FROM all_tasks_by_all_time WHERE date = :userDate AND btn_status = :userBtnStatus")
    fun getAllTasksByDateAndStatus(userDate: Long, userBtnStatus: Int): Flow<List<AllTasksByAllTime>>
    @Query("UPDATE all_tasks_by_all_time SET btn_status = :userBtnStatus WHERE (task = :userTask AND date = :userDate)")
    fun changeBtnStatus(userTask: String, userDate: Long, userBtnStatus: Int)
    @Query("UPDATE all_tasks_by_all_time SET task_status = :userTaskStatus WHERE (task = :userTask AND date = :userDate)")
    fun changeTaskStatus(userTask: String, userDate: Long, userTaskStatus: String)
    @Query("UPDATE all_tasks_by_all_time SET time = :userTime WHERE (task = :userTask AND date = :userDate)")
    fun changeTaskTime(userTask: String, userDate: Long, userTime: Long)
    @Query("SELECT * FROM all_tasks_by_all_time")
    fun getAllTasks(): List<AllTasksByAllTime>
}