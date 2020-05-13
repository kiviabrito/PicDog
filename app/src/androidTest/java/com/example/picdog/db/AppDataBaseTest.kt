package com.example.picdog.db

import androidx.room.Room
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.example.picdog.utility.testUtil.ModelFactory
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Instrumented test for database insert, get data and delete data.
 */
@OptIn(ExperimentalCoroutinesApi::class)
@RunWith(AndroidJUnit4::class)
class AppDataBaseTest {

  private lateinit var database: AppDatabase

  @Before
  fun initDb() {
    database = Room.inMemoryDatabaseBuilder(
      InstrumentationRegistry.getInstrumentation().targetContext,
      AppDatabase::class.java
    ).build()
  }

  @After
  fun closeDb() {
    database.close()
  }

  @Test
  fun insertUser() {
    val user = ModelFactory.createUserEntity()
    runBlocking {
      database.userDao().upsert(user)
      val pokemonList = database.userDao().selectAll()
      assert(pokemonList.isNotEmpty())
    }
  }

  @Test
  fun getUserData() {
    val userList = ModelFactory.createUserEntityList(5)
    runBlocking {
      userList.forEach {
        database.userDao().upsert(it)
      }
      val retrievedPokemon = database.userDao().selectAll()
      assert(retrievedPokemon == userList.sortedWith(compareBy({ it._id }, { it._id })))
    }
  }

  @Test
  fun clearUserData() {
    val user = ModelFactory.createUserEntity()
    runBlocking {
      database.userDao().upsert(user)
      database.userDao().deleteAll()
      assert(database.userDao().selectAll().isEmpty())
    }
  }

  @Test
  fun insertFeed() {
    val feed = ModelFactory.createFeedEntity("husky")
    runBlocking {
      database.feedDao().upsert(feed)
      val pokemonList = database.feedDao().selectAll()
      assert(pokemonList.isNotEmpty())
    }
  }

  @Test
  fun getFeedData() {
    val feed1 = ModelFactory.createFeedEntity("husky")
    val feed2 = ModelFactory.createFeedEntity("hound")
    val feed3 = ModelFactory.createFeedEntity("pug")
    val feedList = listOf(feed1, feed2, feed3)
    runBlocking {
      feedList.forEach {
        database.feedDao().upsert(it)
      }
      val retrievedPokemon = database.userDao().selectAll()
      assert(retrievedPokemon == feedList.sortedWith(compareBy({ it.category }, { it.category })))
    }
  }

  @Test
  fun clearFeedData() {
    val feed = ModelFactory.createFeedEntity("husky")
    runBlocking {
      database.feedDao().upsert(feed)
      database.feedDao().deleteAll()
      assert(database.feedDao().selectAll().isEmpty())
    }
  }
}
