package com.example.picdog.db
import androidx.room.*
import com.example.picdog.model.FeedEntity
import com.example.picdog.model.UserEntity


@Dao
interface FeedDao {

  @Query("SELECT * FROM feedentity")
  fun selectAll(): List<FeedEntity>

  @Query("SELECT * FROM feedentity WHERE category LIKE :category LIMIT 1")
  fun findByCategory(category: String): FeedEntity?

  @Query("DELETE FROM feedentity")
  fun deleteAll()

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