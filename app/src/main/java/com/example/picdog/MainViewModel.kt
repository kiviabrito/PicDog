package com.example.picdog

import androidx.lifecycle.*
import com.example.picdog.model.ErrorResponse
import com.example.picdog.network.PicDogService
import com.example.picdog.utility.SingleLiveData
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.lang.Exception

class MainViewModel(
  private val service: PicDogService = App.picDogService
) : ViewModel() {

  // Handle Error
  val error: SingleLiveData<String> = SingleLiveData()

  // Handle RecyclerView
  private val _feed = MutableLiveData<ArrayList<String>>()
  val feed: LiveData<ArrayList<String>> = _feed

  fun setIndex(index: Int) {
    when (index) {
      1 -> {
        getFeed("husky")
      }
      2 -> {
        getFeed("hound")
      }
      3 -> {
        getFeed("pug")
      }
      4 -> {
        getFeed("labrador")
      }
    }
  }

  private fun getFeed(category: String) {
    viewModelScope.launch(Dispatchers.Default) {
      try {
        val response = service.feedRequest(category, "token")
        if (response.isSuccessful) {
          val photoArray = arrayListOf<String>()
          _feed.postValue(response.body()?.list?.flatMapTo(photoArray) { arrayListOf(it) })
        } else {
          val reader = response.errorBody()?.charStream()
          val errorResponse = Gson().fromJson(reader, ErrorResponse::class.java)
          error.postValue(errorResponse.error.message)
        }
      } catch (e: Exception) {
        error.postValue(e.message)
      }
    }
  }

}