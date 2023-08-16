package com.example.diploma.domain

data class ListItem(var text: String = "", var description: String = "", var status: String = "",
                    var showItem: Boolean = false, var currentTime: Long = 0L,
                    var timerStarted:Boolean = false, var onPause:Boolean = false,
                    var returnItem: Boolean = false, var isDescription: Boolean = false,
                    var isRunning: Boolean = false, var isStop: Boolean = false)
