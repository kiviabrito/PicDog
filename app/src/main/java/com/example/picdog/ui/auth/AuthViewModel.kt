package com.example.picdog.ui.auth

import androidx.lifecycle.*
import com.example.picdog.App
import com.example.picdog.db.AppDatabase
import com.example.picdog.model.ErrorResponse
import com.example.picdog.model.SignupResponse
import com.example.picdog.network.PicDogService
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class AuthViewModel(
  private val service: PicDogService = App.picDogService,
  private val database: AppDatabase = App.db
) : ViewModel() {

  // Handle Response
  private val _signUpResponse = MutableLiveData<SignupResponse>()
  val signUpResponse: LiveData<SignupResponse> = _signUpResponse

  fun signUp(email: String) {
    viewModelScope.launch(Dispatchers.Default) {
      try {
        val response = service.signupRequest(email)
        if (response.isSuccessful) {
          database.userDao().upsert(response.body()!!.user)
          _signUpResponse.postValue(SignupResponse.Success)
        } else {
          val reader = response.errorBody()?.charStream()
          val errorResponse = Gson().fromJson(reader, ErrorResponse::class.java)
          _signUpResponse.postValue(SignupResponse.Failure(errorResponse.error.message))
        }
      } catch (e: Exception) {
        _signUpResponse.postValue(SignupResponse.Failure(e.message ?: "Connection Error"))
      }
    }
  }

}