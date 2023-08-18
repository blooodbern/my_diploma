package com.example.diploma.data.dbcommands

import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.diploma.data.tables.SetupData

interface DaoSetupData {
    @Insert (onConflict = OnConflictStrategy.REPLACE)
    fun insertSettings(settings: SetupData)
    @Query("SELECT setting_value FROM setup_data WHERE setting_name = :settingsName")
    fun getSettings(settingsName:String):Int
    @Query("SELECT COUNT(*) FROM setup_data")
    fun checkIfSettingsTableEmpty():Int
}