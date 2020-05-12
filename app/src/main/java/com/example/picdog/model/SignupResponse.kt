package com.example.picdog.model

sealed class SignupResponse {

  object Success : SignupResponse()
  data class Failure(val message: String) : SignupResponse()

}