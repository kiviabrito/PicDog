package com.example.picdog.db
import androidx.room.*
import com.example.picdog.model.FeedEntity
import com.example.picdog.model.UserEntity


@Dao
interface FeedDao {

  @Query("SELECT * FROM feedentity")
  suspend fun selectAll(): List<FeedEntity>

  @Query("SELECT * FROM feedentity WHERE category LIKE :category LIMIT 1")
  suspend fun findByCategory(category: String): FeedEntity?

  @Query("DELETE FROM feedentity")
  suspend fun deleteAll()

  @Delete
  suspend fun delete(pokemon: FeedEntity)

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insert(entity: FeedEntity): Long

  @Update(onConflict = OnConflictStrategy.REPLACE)
  suspend fun update(entity: FeedEntity)

  @Transaction
  suspend fun upsert(entity: FeedEntity) {
    val id = insert(entity)
    if (id == -1L) {
      update(entity)
    }
  }
}