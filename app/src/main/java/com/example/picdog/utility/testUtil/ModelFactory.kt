package com.example.picdog.utility.testUtil

import com.example.picdog.model.FeedEntity
import com.example.picdog.model.UserEntity
import java.util.*
import kotlin.random.Random

class ModelFactory {

  companion object {

    fun createUserEntity(): UserEntity {
      return UserEntity(
        _id = UUID.randomUUID().toString(),
        token = UUID.randomUUID().toString(),
        createdAt = Date().toString(),
        updatedAt = Date().toString(),
        __v = 0
      )
    }

    fun createUserEntityList(size: Int): List<UserEntity> {
      val list = mutableListOf<UserEntity>()
      for (i in 0 until size + 1) {
        list.add(createUserEntity())
      }
      return list
    }

    fun createFeedEntity(category: String): FeedEntity {
      return FeedEntity(
        category = category,
        list = listOf(
          UUID.randomUUID().toString(),
          UUID.randomUUID().toString(),
          UUID.randomUUID().toString(),
          UUID.randomUUID().toString()
        )
      )
    }

  }

}