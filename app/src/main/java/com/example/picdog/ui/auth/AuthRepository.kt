package com.example.picdog.ui.auth

import androidx.lifecycle.LiveData
import com.example.picdog.App
import com.example.picdog.db.AppDatabase
import com.example.picdog.model.UserResponse
import com.example.picdog.api.*
import com.example.picdog.utility.ApiSuccessResponse
import com.example.picdog.utility.DataState
import com.example.picdog.utility.GenericApiResponse
import com.example.picdog.utility.NetworkBoundResource

/**
 * getUser(eamil: String) - first creatCall() is called and fetches user from server, once it gets the response
 * handleApiSuccessResponse() is called and it updates the database and the DataState.
 */

class AuthRepository(
  val service: PicDogService = App.picDogService,
  val dataBase: AppDatabase = App.db
) {

  fun getUser(email: String): LiveData<DataState<AuthViewState>> {
    return object : NetworkBoundResource<UserResponse, AuthViewState>() {

      override suspend fun createCall(): LiveData<GenericApiResponse<UserResponse>> {
        return service.signupRequest(email)
      }

      override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<UserResponse>) {
        // Update DataBase
        dataBase.userDao().upsert(response.body.user)
        // Set New DataState
        result.value = DataState.data(
          null,
          AuthViewState(
            user = true
          )
        )
      }

      override suspend fun loadFromDb() {}

    }.asLiveData()
  }

}