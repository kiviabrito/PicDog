package com.example.picdog.utility

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

abstract class NetworkBoundResource<ResponseObject, ViewStateType> {

  protected val result = MediatorLiveData<DataState<ViewStateType>>()

  init {
    result.value = DataState.loading(true)
    GlobalScope.launch(IO) {
      handleDataBase()
      val apiResponse = createCall()
      withContext(Main) {
        result.addSource(apiResponse) { response ->
          handleResponse(apiResponse, response)
        }
      }
    }
  }

  private fun handleResponse(
    apiResponse: LiveData<GenericApiResponse<ResponseObject>>,
    response: GenericApiResponse<ResponseObject>
  ) {
    GlobalScope.launch(Main) {
      result.removeSource(apiResponse)
      handleNetworkCall(response)
    }
  }

  private suspend fun handleNetworkCall(response: GenericApiResponse<ResponseObject>) {

    when (response) {
      is ApiSuccessResponse -> {
        handleApiSuccessResponse(response)
      }
      is ApiErrorResponse -> {
        println("DEBUG: NetworkBoundResource: ${response.errorMessage}")
        onReturnError(response.errorMessage)
      }
      is ApiEmptyResponse -> {
        println("DEBUG: NetworkBoundResource: Request returned NOTHING (HTTP 204)")
        onReturnError("HTTP 204. Returned NOTHING.")
      }
    }
  }

  private fun onReturnError(message: String) {
    result.value = DataState.error(message)
  }

  abstract suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<ResponseObject>)

  abstract suspend fun createCall(): LiveData<GenericApiResponse<ResponseObject>>

  abstract suspend fun handleDataBase()

  fun asLiveData() = result as LiveData<DataState<ViewStateType>>
}

