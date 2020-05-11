package com.example.picdog.db
import androidx.room.*
import com.example.picdog.model.FeedEntity
import com.example.picdog.model.UserEntity


@Dao
interface FeedDao {

  @Query("SELECT * FROM FeedEntity")
  fun selectAll(): List<FeedEntity>

  @Query("SELECT * FROM FeedEntity WHERE category LIKE :category")
  fun findByCategory(category: String): FeedEntity

  @Delete
  fun delete(pokemon: FeedEntity)

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  fun insert(entity: FeedEntity): Long

  @Update(onConflict = OnConflictStrategy.REPLACE)
  fun update(entity: FeedEntity)

  @Transaction
  fun upsert(entity: FeedEntity) {
    val id = insert(entity)
    if (id == -1L) {
      update(entity)
    }
  }
}