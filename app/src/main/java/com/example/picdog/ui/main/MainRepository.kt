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

class MainRepository(
  val service: PicDogService = App.picDogService,
  val dataBase: AppDatabase = App.db,
  private val glideCache: GlideCache = App.glideCache
) {

  fun getFeed(category: String): LiveData<DataState<MainViewState>> {
    return object : NetworkBoundResource<FeedEntity, MainViewState>() {

      override suspend fun createCall(): LiveData<GenericApiResponse<FeedEntity>> {
        val token = dataBase.userDao().selectAll().first().token
        return service.feedRequest(category, token)
      }

      override suspend fun handleApiSuccessResponse(response: ApiSuccessResponse<FeedEntity>) {
        withContext(Default) {
          val feedFromDb = dataBase.feedDao().findByCategory(category)
          val feedFromResponse = response.body

          // If New Data
          if (feedFromDb != feedFromResponse) {
            val photoArray = arrayListOf<String>()
            feedFromResponse.list.flatMapTo(photoArray) { arrayListOf(it) }

            // Set New DataState
            setMainViewState(photoArray, result)

            // Update DataBase
            dataBase.feedDao().upsert(feedFromResponse)

            // Cache Image
            glideCache.cachePictures(photoArray)
          }
        }
      }

      override suspend fun handleDataBase() {
        val photoArray = arrayListOf<String>()
        val feed = dataBase.feedDao().findByCategory(category)
        feed?.let {
          it.list.flatMapTo(photoArray) { url -> arrayListOf(url) }

          // Set New DataState
          setMainViewState(photoArray, result)
        }
      }

    }.asLiveData()
  }

  suspend fun setMainViewState(arrayList: ArrayList<String>, result: MediatorLiveData<DataState<MainViewState>>) {
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
        dataBase.userDao().deleteAll()
        dataBase.feedDao().deleteAll()
        glideCache.clearCache()
      }
      // Set New DataState
      MutableLiveData(
        (DataState.data(
          null,
          MainViewState(
            feed = arrayListOf(),
            isSignOut = true
          )
        ))
      )
    } catch (e: Exception) {
      MutableLiveData((DataState.error(e.message ?: "Unknown")))
    }
  }

}