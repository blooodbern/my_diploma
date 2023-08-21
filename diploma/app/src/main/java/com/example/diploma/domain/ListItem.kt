package com.example.diploma.domain

data class ListItem(var id:Int, var text:String, var description:String, var date:Long,
                    var time:Long, var isPlaying:Boolean, var isStop:Boolean,
                    var status:Int, var isVisible:Boolean, var lastChanged:Boolean)

/*
data class ListItem(var id:Int = -1, var text: String = "",
                    var description: String = "", var status: Int = 0,
                    var showItem: Boolean = false, var currentTime: Long = 0L,
                    var timerStarted:Boolean = false, var isStop:Boolean = false,
                    var returnItem: Boolean = false, var isDescription: Boolean = false,
                    var isRunning: Boolean = false, var onPause: Boolean = false,
                    var isVisible:Boolean=false)

 */
