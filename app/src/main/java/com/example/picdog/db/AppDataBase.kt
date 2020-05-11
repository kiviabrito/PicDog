package com.example.picdog.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.picdog.model.FeedEntity
import com.example.picdog.model.UserEntity

@TypeConverters(Converters::class)
@Database(
  entities = [
    UserEntity::class,
    FeedEntity::class
  ], version = 1
)
abstract class AppDatabase : RoomDatabase() {

  abstract fun feedDao(): FeedDao
  abstract fun userDao(): UserDao

  companion object {

    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getInstance(context: Context): AppDatabase =
      INSTANCE ?: synchronized(this) {
        INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
      }

    private fun buildDatabase(context: Context) =
      Room.databaseBuilder(
        context.applicationContext,
        AppDatabase::class.java, "picdog.db"
      )
        .build()
  }

}