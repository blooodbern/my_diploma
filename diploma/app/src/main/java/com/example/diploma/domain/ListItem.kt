package com.example.diploma.domain

data class ListItem(var id:Int = -1, var text: String = "",
                    var description: String = "", var status: Int = 0,
                    var showItem: Boolean = false, var currentTime: Long = 0L,
                    var timerStarted:Boolean = false, var onPause:Boolean = false,
                    var returnItem: Boolean = false, var isDescription: Boolean = false,
                    var isRunning: Boolean = false, var isStop: Boolean = false,
                    var isVisible:Boolean=false)
