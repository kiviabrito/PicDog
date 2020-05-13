package com.example.picdog.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.picdog.App
import com.example.picdog.db.FeedDao
import com.example.picdog.db.UserDao
import com.example.picdog.model.ErrorResponse
import com.example.picdog.model.FeedEntity
import com.example.picdog.network.NoConnectivityException
import com.example.picdog.network.PicDogService
import com.example.picdog.utility.GlideCache
import com.example.picdog.utility.SingleLiveData
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class MainViewModel(
  private val service: PicDogService = App.picDogService,
  private val userDao: UserDao = App.db.userDao(),
  private val feedDao: FeedDao = App.db.feedDao(),
  private val glide: GlideCache? = App.glideCache
) : ViewModel() {

  val errorResponse : SingleLiveData<String> = SingleLiveData()

  private val _feedResponse: MutableLiveData<List<String>> = MutableLiveData()
  val feedResponse: LiveData<List<String>> = _feedResponse

  private val _expandedPicture: MutableLiveData<String> = MutableLiveData()
  val expandedPicture: LiveData<String> = _expandedPicture

  var isConnected = true

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

  fun getFeed(category: String) {
    viewModelScope.launch(Dispatchers.IO) {
      try {
        val feed = feedDao.findByCategory(category)
        feed?.let {
          _feedResponse.postValue(feed.list)
        }
        if (isConnected) {
          requestUpdateFromNetwork(category, feed)
        }
      } catch (e: Exception) {
        postError(e)
      }
    }
  }

  suspend fun requestUpdateFromNetwork(category: String, feed: FeedEntity?) {
    try {
      val user = userDao.selectAll().firstOrNull()
      user?.let { userEntity ->
        val response = service.feedRequest(category, userEntity.token)
        if (response.isSuccessful) {
          if (feed != response.body()) {
            _feedResponse.postValue(response.body()!!.list)
            feedDao.upsert(response.body()!!)
            glide?.cachePictures(response.body()!!.list)
          }
        } else {
          val reader = response.errorBody()?.charStream()
          val errorResponse = Gson().fromJson(reader, ErrorResponse::class.java)
          this.errorResponse.postValue(errorResponse.error.message)
        }
      }
    } catch (e: Exception) {
      postError(e)
    }
  }

  fun postError(e: Exception) {
    isConnected = e.message != NoConnectivityException.MESSAGE
    errorResponse.postValue(e.message ?: NoConnectivityException.MESSAGE)
  }

  fun setExpandedPicture(picture: String) {
    _expandedPicture.postValue(picture)
  }

  fun signOut() : Boolean {
    return try {
      viewModelScope.launch(Dispatchers.IO) {
        userDao.deleteAll()
        feedDao.deleteAll()
        glide?.clearCache()
      }
      true
    } catch (e: Exception) {
      errorResponse.postValue(e.message ?: "Unknown")
      false
    }
  }

}