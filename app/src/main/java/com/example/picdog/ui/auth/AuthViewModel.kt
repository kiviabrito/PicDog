package com.example.picdog.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.picdog.App
import com.example.picdog.db.AppDatabase
import com.example.picdog.model.ErrorResponse
import com.example.picdog.model.SignupResponse
import com.example.picdog.network.PicDogService
import com.example.picdog.utility.SingleLiveData
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel(
  private val service: PicDogService = App.picDogService,
  private val database: AppDatabase = App.db
) : ViewModel() {

  // Handle Response
  val signUpResponse: SingleLiveData<SignupResponse> = SingleLiveData()

  // Handle Sign Up Check
  val isSignUp: SingleLiveData<Boolean> = SingleLiveData()

  fun signUp(email: String) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val response = service.signupRequest(email)
        if (response.isSuccessful) {
          database.userDao().upsert(response.body()!!.user)
          signUpResponse.postValue(SignupResponse.Success)
        } else {
          val reader = response.errorBody()?.charStream()
          val errorResponse = Gson().fromJson(reader, ErrorResponse::class.java)
          signUpResponse.postValue(SignupResponse.Failure(errorResponse.error.message))
        }
      } catch (e: Exception) {
        signUpResponse.postValue(SignupResponse.Failure(e.message ?: "Internet Connection"))
      }
    }
  }

  fun checkIfIsSignUp() {
    viewModelScope.launch(Dispatchers.Default) {
      val user = App.db.userDao().selectAll().firstOrNull()
      if (user == null) {
        isSignUp.postValue(false)
      } else {
        isSignUp.postValue(true)
      }
    }
  }

}