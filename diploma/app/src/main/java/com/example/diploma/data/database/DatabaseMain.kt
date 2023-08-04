package com.example.diploma.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.diploma.data.dbcommands.DaoAllTksByAllT
import com.example.diploma.data.tables.AllTasksByAllTime

@Database(
    entities = [AllTasksByAllTime::class],
    version = 1
)
abstract class DatabaseMain: RoomDatabase() {
    abstract fun getDaoMain(): DaoAllTksByAllT
    companion object{
        fun getDatabase(context: Context):DatabaseMain{
            return Room.databaseBuilder(
                context.applicationContext,
                DatabaseMain::class.java,
                "BloodbernDiplomaDB"
            ).build()
        }
    }
}