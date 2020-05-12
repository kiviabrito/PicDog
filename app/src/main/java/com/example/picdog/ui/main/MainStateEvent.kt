package com.example.picdog.ui.main

sealed class MainStateEvent {

    class GetFeedEvent(
        val section: Int
    ): MainStateEvent()

    object TappedSignOut : MainStateEvent()

    object None : MainStateEvent()

}
