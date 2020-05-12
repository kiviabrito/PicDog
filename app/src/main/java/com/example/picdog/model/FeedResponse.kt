package com.example.picdog.model

sealed class FeedResponse {

  data class Success(val list: ArrayList<String>) : FeedResponse()
  data class Failure(val message: String) : FeedResponse()

}