package com.example.picdog.ui.auth

sealed class AuthStateEvent {

    class GetUserEvent(
        val email: String
    ): AuthStateEvent()

    object None : AuthStateEvent()

}