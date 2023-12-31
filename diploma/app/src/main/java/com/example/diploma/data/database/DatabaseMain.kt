package com.example.diploma.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.diploma.data.dbcommands.DaoAllTksByAllT
import com.example.diploma.data.dbcommands.DaoSetupData
import com.example.diploma.data.dbcommands.DaoTodayList
import com.example.diploma.data.tables.AllTasksByAllTime
import com.example.diploma.data.tables.SetupData
import com.example.diploma.data.tables.TodayList

@Database(
    entities = [AllTasksByAllTime::class, TodayList::class, SetupData::class],
    version = 1
)
abstract class DatabaseMain: RoomDatabase() {
    abstract fun getDaoMain(): DaoAllTksByAllT
    abstract fun getDaoTodayList(): DaoTodayList
    abstract fun getDaoSetupData(): DaoSetupData
    companion object{
        fun getDatabase(context: Context):DatabaseMain{
            return Room.databaseBuilder(
                context.applicationContext,
                DatabaseMain::class.java,
                "BloodbernDiplomaDB3.16"
            ).build()
        }
    }
}