package com.example.diploma.data.dbcommands

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.diploma.data.tables.SetupData

@Dao
interface DaoSetupData {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertSettings(settings: SetupData)
    @Query("SELECT setting_value FROM setup_data WHERE setting_name = :settingsName")
    fun getSettings(settingsName:String):Int
    @Query("SELECT setting_name FROM setup_data WHERE id = :id")
    fun getSettingsName(id:Int):String
    @Query("UPDATE setup_data SET setting_value = :settingsValue WHERE setting_name = :settingsName")
    fun setSettings(settingsName:String, settingsValue:Int)
    @Query("SELECT COUNT(*) FROM setup_data")
    fun checkIfSettingsTableEmpty():Int
}