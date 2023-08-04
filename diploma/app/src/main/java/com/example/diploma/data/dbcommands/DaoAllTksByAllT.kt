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
    @Query("SELECT COUNT(*) FROM all_tasks_by_all_time WHERE task = :userTask")
    fun checkIfTaskExists(userTask: String): Int
    @Query("SELECT * FROM all_tasks_by_all_time WHERE date = :userDate")
    fun getAllTasksByDate(userDate: Long): Flow<List<AllTasksByAllTime>>
    @Query("SELECT * FROM all_tasks_by_all_time WHERE date = :userDate AND btn_status = :userBtnStatus")
    fun getAllTasksByDateAndStatus(userDate: Long, userBtnStatus: Int): Flow<List<AllTasksByAllTime>>
}