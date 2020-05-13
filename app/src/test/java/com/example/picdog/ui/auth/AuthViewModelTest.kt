package com.example.picdog.ui.auth

import com.example.picdog.db.UserDao
import com.example.picdog.model.*
import com.example.picdog.network.PicDogService
import com.example.picdog.testUtility.InstantExecutorExtension
import com.example.picdog.utility.testUtil.ModelFactory
import com.google.gson.Gson
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.any
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
class AuthViewModelTest {

  private val USER1: UserEntity = ModelFactory.createUserEntity()

  private lateinit var authViewModel: AuthViewModel

  private var userDao: UserDao = mock()
  private var service: PicDogService = mock()

  @BeforeEach
  fun initEach() {
    reset(userDao)
    reset(service)
    authViewModel = AuthViewModel(service, userDao)
  }

  @AfterEach
  fun tearDown() {
  }

  @ExperimentalCoroutinesApi
  @org.junit.jupiter.api.Test
  @Throws(Exception::class)
  fun signUp_returnSuccess() {
    runBlockingTest {
      // ARRANGE
      val returnedData: Response<UserResponse> = Response.success(UserResponse(USER1))
      whenever(service.signupRequest(any())).thenReturn(returnedData)
      whenever(userDao.upsert(any())).thenReturn(print("inserted"))
      // ACT
      authViewModel.signUp("test@gmail.com")
      // ASSERT
      com.nhaarman.mockitokotlin2.verify(service).signupRequest(any())
      com.nhaarman.mockitokotlin2.verify(userDao).upsert(any())
      assertEquals(authViewModel.signUpResponse.value, SignupResponse.Success)
    }
  }

  @ExperimentalCoroutinesApi
  @org.junit.jupiter.api.Test
  @Throws(Exception::class)
  fun signUp_returnFailure() {
    runBlockingTest {
      // ARRANGE
      val returnedData: Response<UserResponse> = Response.error(
        400,
        ResponseBody.create(null, Gson().toJson(ErrorResponse(Message("Email is not valid"))))
      )
      whenever(service.signupRequest(any())).thenReturn(returnedData)
      // ACT
      authViewModel.signUp("test@")
      // ASSERT
      com.nhaarman.mockitokotlin2.verify(service).signupRequest(any())
      assertEquals(authViewModel.signUpResponse.value, SignupResponse.Failure("Email is not valid"))
    }
  }

  @ExperimentalCoroutinesApi
  @org.junit.jupiter.api.Test
  @Throws(Exception::class)
  fun checkIfIsSignUp_returnTrue() {
    runBlockingTest {
      // ARRANGE
      whenever(userDao.selectAll()).thenReturn(listOf(USER1))
      // ACT
      authViewModel.checkIfIsSignUp()
      // ASSERT
      com.nhaarman.mockitokotlin2.verify(userDao).selectAll()
      assertEquals(authViewModel.isSignUp.value, true)
    }
  }

  @ExperimentalCoroutinesApi
  @org.junit.jupiter.api.Test
  @Throws(Exception::class)
  fun checkIfIsSignUp_returnFalse() {
    runBlockingTest {
      // ARRANGE
      whenever(userDao.selectAll()).thenReturn(listOf())
      // ACT
      authViewModel.checkIfIsSignUp()
      // ASSERT
      com.nhaarman.mockitokotlin2.verify(userDao).selectAll()
      assertEquals(authViewModel.isSignUp.value, false)
    }
  }

}
