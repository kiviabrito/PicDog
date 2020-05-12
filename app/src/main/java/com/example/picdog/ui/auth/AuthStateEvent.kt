package com.example.picdog.ui.auth

/**
 * Class with possible interactions/events made by the user on AuthActivity.
 */

sealed class AuthStateEvent {

    class SignUpTaped(
        val email: String
    ): AuthStateEvent()

    object None : AuthStateEvent()

}