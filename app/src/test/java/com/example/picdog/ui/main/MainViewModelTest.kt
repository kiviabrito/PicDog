package com.example.picdog.ui.main

import com.example.picdog.db.FeedDao
import com.example.picdog.db.UserDao
import com.example.picdog.model.ErrorResponse
import com.example.picdog.model.FeedEntity
import com.example.picdog.model.Message
import com.example.picdog.model.UserEntity
import com.example.picdog.network.PicDogService
import com.example.picdog.testUtility.InstantExecutorExtension
import com.example.picdog.utility.testUtil.ModelFactory
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.whenever
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runBlockingTest
import okhttp3.ResponseBody
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.Mockito.reset
import retrofit2.Response


/**
 * Test for the AuthViewModel
 */
@ExtendWith(InstantExecutorExtension::class)
class MainViewModelTest {

  private val USER1: UserEntity = ModelFactory.createUserEntity()
  private val FEEDHUSKY1 = ModelFactory.createFeedEntity("husky")
  private val FEEDHUSKY2 = ModelFactory.createFeedEntity("husky")

  private lateinit var mainViewModel: MainViewModel

  private var userDao: UserDao = mock()
  private var feedDao: FeedDao = mock()
  private var service: PicDogService = mock()

  @BeforeEach
  fun initEach() {
    reset(service)
    reset(userDao)
    reset(feedDao)
    mainViewModel = MainViewModel(service, userDao, feedDao, null)
  }

  @AfterEach
  fun tearDown() {
  }

  @ExperimentalCoroutinesApi
  @org.junit.jupiter.api.Test
  @Throws(Exception::class)
  fun getFeed_returnFeedFromDataBase() {
    runBlockingTest {
      // ARRANGE
      whenever(feedDao.findByCategory(any())).thenReturn(FEEDHUSKY1)
      // ACT
      mainViewModel.getFeed("husky")
      // ASSERT
      com.nhaarman.mockitokotlin2.verify(feedDao).findByCategory(any())
      assertEquals(mainViewModel.feedResponse.value, FEEDHUSKY1.list)
    }
  }

  @ExperimentalCoroutinesApi
  @org.junit.jupiter.api.Test
  @Throws(Exception::class)
  fun requestUpdateFromNetwork_returnDifferentFeed() {
    runBlockingTest {
      // ARRANGE
      val returnedData: Response<FeedEntity> = Response.success(FEEDHUSKY2)
      whenever(userDao.selectAll()).thenReturn(listOf(USER1))
      whenever(service.feedRequest(any(), any())).thenReturn(returnedData)
      // ACT
      mainViewModel.requestUpdateFromNetwork("husky", FEEDHUSKY1)
      // ASSERT
      com.nhaarman.mockitokotlin2.verify(userDao).selectAll()
      com.nhaarman.mockitokotlin2.verify(service).feedRequest(any(), any())
      assertEquals(mainViewModel.feedResponse.value, FEEDHUSKY2.list)
    }
  }

  @ExperimentalCoroutinesApi
  @org.junit.jupiter.api.Test
  @Throws(Exception::class)
  fun requestUpdateFromNetwork_returnSameFeed() {
    runBlockingTest {
      // ARRANGE
      val returnedData: Response<FeedEntity> = Response.success(FEEDHUSKY1)
      whenever(userDao.selectAll()).thenReturn(listOf(USER1))
      whenever(service.feedRequest(any(), any())).thenReturn(returnedData)
      // ACT
      mainViewModel.requestUpdateFromNetwork("husky", FEEDHUSKY1)
      // ASSERT
      com.nhaarman.mockitokotlin2.verify(userDao).selectAll()
      com.nhaarman.mockitokotlin2.verify(service).feedRequest(any(), any())
      assertEquals(mainViewModel.feedResponse.value, null)
    }
  }

  @ExperimentalCoroutinesApi
  @org.junit.jupiter.api.Test
  @Throws(Exception::class)
  fun requestUpdateFromNetwork_returnFailure() {
    runBlockingTest {
      // ARRANGE
      val returnedData: Response<FeedEntity> = Response.error(
        400,
        ResponseBody.create(null, Gson().toJson(ErrorResponse(Message("Show this Message"))))
      )
      whenever(userDao.selectAll()).thenReturn(listOf(USER1))
      whenever(service.feedRequest(any(), any())).thenReturn(returnedData)
      // ACT
      mainViewModel.requestUpdateFromNetwork("husky", FEEDHUSKY1)
      // ASSERT
      com.nhaarman.mockitokotlin2.verify(userDao).selectAll()
      com.nhaarman.mockitokotlin2.verify(service).feedRequest(any(), any())
      assertEquals(mainViewModel.errorResponse.value, "Show this Message")
    }
  }

  @org.junit.jupiter.api.Test
  @Throws(Exception::class)
  fun postError_returnErrorInternetConnection() {
    // ARRANGE
    // ACT
    mainViewModel.postError(java.lang.Exception("Internet Connection"))
    // ASSERT
    assertEquals(mainViewModel.isConnected, false)
    assertEquals(mainViewModel.errorResponse.value, "Internet Connection")
  }

  @org.junit.jupiter.api.Test
  @Throws(Exception::class)
  fun postError_returnErrorMessage() {
    // ARRANGE
    // ACT
    mainViewModel.postError(java.lang.Exception("Another Error"))
    // ASSERT
    assertEquals(mainViewModel.isConnected, true)
    assertEquals(mainViewModel.errorResponse.value, "Another Error")
  }

  @org.junit.jupiter.api.Test
  @Throws(Exception::class)
  fun setExpandedPicture_returnPicture() {
    // ARRANGE
    // ACT
    mainViewModel.setExpandedPicture("")
    // ASSERT
    assertEquals(mainViewModel.expandedPicture.value, "")
  }

}
