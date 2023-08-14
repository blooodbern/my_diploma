package com.example.diploma.data

import android.provider.Settings.Global.getString
import com.example.diploma.R
import kotlin.properties.Delegates

class STORAGE {
    companion object{
        var IsPressed = false
        var curTask = ""
        var curDate:Long = 0L
        var maxPosition:Int = -1
        var DatabaseTodayListAlreadyCreated = false
        var TodayListAlreadyCreated = false
        const val LIST_LIMIT = 5
        const val IS_RUNNING = 1
        const val ON_PAUSE = 2
        const val STOP = 3
        const val IN_PROCESS = 4
        const val UNSATISFACTORY = 5
        const val PARTLY = 6
        const val SUCCESS = 7
        const val NOT_STARTED = 8
    }
}