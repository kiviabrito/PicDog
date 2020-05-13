package com.example.picdog.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.picdog.App
import com.example.picdog.db.UserDao
import com.example.picdog.model.ErrorResponse
import com.example.picdog.model.SignupResponse
import com.example.picdog.model.UserEntity
import com.example.picdog.model.UserResponse
import com.example.picdog.network.NoConnectivityException
import com.example.picdog.network.PicDogService
import com.example.picdog.utility.SingleLiveData
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AuthViewModel(
  private val service: PicDogService = App.picDogService,
  private val userDao: UserDao = App.db.userDao()
) : ViewModel() {

  // Handle Response
  val signUpResponse: SingleLiveData<SignupResponse> = SingleLiveData()
  var test = signUpResponse.value

  // Handle Sign Up Check
  val isSignUp: SingleLiveData<Boolean> = SingleLiveData()

  fun signUp(email: String) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val response = service.signupRequest(email)
        if (response.isSuccessful) {
          test = SignupResponse.Success
          userDao.upsert(response.body()!!.user)
          signUpResponse.postValue(SignupResponse.Success)
        } else {
          val reader = response.errorBody()?.charStream()
          val errorResponse = Gson().fromJson(reader, ErrorResponse::class.java)
          signUpResponse.postValue(SignupResponse.Failure(errorResponse.error.message))
        }
      } catch (e: Exception) {
        signUpResponse.postValue(SignupResponse.Failure(e.message ?: NoConnectivityException.MESSAGE))
      }
    }
  }

  fun checkIfIsSignUp() {
    viewModelScope.launch(Dispatchers.Default) {
      val user = userDao.selectAll().firstOrNull()
      if (user == null) {
        isSignUp.postValue(false)
      } else {
        isSignUp.postValue(true)
      }
    }
  }

}