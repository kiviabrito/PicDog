package com.example.picdog.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.example.picdog.App
import com.example.picdog.db.AppDatabase
import com.example.picdog.model.FeedEntity
import com.example.picdog.api.*
import com.example.picdog.utility.*
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * getFeed(category: String) - first loadFromDb() is called and fetches data from the database and updates the DataState
 * IF there are any data. Right after, creatCall() is called and fetches data from server, once it gets the response
 * handleApiSuccessResponse() is called and verify if any data is different, if so, it updates the database and the
 * DataState.
 *
 * signOut() - Deletes all database data and glide cache, and so returns a DataState.data in case the task is completed
 * and DataState.error in case of any error.
 */

class MainRepository(
  val service: PicDogService = App.picDogService,
  val database: AppDatabase = App.db,
  private val glideCache: GlideCache = App.glideCache
) {

  fun getFeed(category: String): LiveData<DataState<MainViewState>> {
    return object : NetworkBoundResource<FeedEntity, MainViewState>() {

      override suspend fun loadFromDb() {
        val feed = database.feedDao().findByCategory(category)
        feed?.let {
          setMainViewState(it.list, result)
        }
      }

      override suspend fun createCall(): LiveData<GenericApiResponse<FeedEntity>> {
        val token = database.userDao().selectAll().first().token
        return service.feedRequest(category, token)
      }

      override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<FeedEntity>) {
        withContext(Default) {
          val feedFromDb = database.feedDao().findByCategory(category)
          val feedFromResponse = response.body
          if (feedFromDb != feedFromResponse) {
            // Set New DataState
            setMainViewState(feedFromResponse.list, result)
            // Update DataBase
            database.feedDao().upsert(feedFromResponse)
            // Cache Image
            glideCache.cachePictures(feedFromResponse.list)
          }
        }
      }

    }.asLiveData()
  }

  suspend fun setMainViewState(arrayList: List<String>, result: MediatorLiveData<DataState<MainViewState>>) {
    withContext(Main) {
      result.value = DataState.data(
        null,
        MainViewState(
          feed = arrayList,
          isSignOut = false
        )
      )
    }
  }

  fun signOut(): LiveData<DataState<MainViewState>> {
    return try {
      // Delete Data
      GlobalScope.launch(Default) {
        database.userDao().deleteAll()
        database.feedDao().deleteAll()
        glideCache.clearCache()
      }
      // Set New DataState
      MutableLiveData(
        (DataState.data(
          null,
          MainViewState(
            feed = listOf(),
            isSignOut = true
          )
        ))
      )
    } catch (e: Exception) {
      MutableLiveData((DataState.error(e.message ?: "Unknown")))
    }
  }

}