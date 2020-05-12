package com.example.picdog.utility

interface DataStateListener {
    fun onDataStateChange(dataState: DataState<*>?)
}