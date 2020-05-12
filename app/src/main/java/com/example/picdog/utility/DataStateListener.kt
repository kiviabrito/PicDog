package com.example.picdog.utility

import com.example.picdog.utility.DataState

interface DataStateListener {

    fun onDataStateChange(dataState: DataState<*>?)
}