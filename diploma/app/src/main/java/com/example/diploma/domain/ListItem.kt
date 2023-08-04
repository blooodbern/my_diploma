package com.example.diploma.domain

data class ListItem(val text: String, var isDescription: Boolean = false,
                    var isRunning: Boolean = false, var isStop: Boolean = false,
                    var currentTime: Long = 0L)
