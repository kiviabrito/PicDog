package com.example.picdog.db
import androidx.room.*
import com.example.picdog.model.UserEntity


@Dao
interface UserDao {

  @Query("SELECT * FROM userentity")
  suspend fun selectAll(): List<UserEntity>

  @Query("DELETE FROM userentity")
  suspend fun deleteAll()

  @Delete
  suspend fun delete(pokemon: UserEntity)

  @Insert(onConflict = OnConflictStrategy.IGNORE)
  suspend fun insert(entity: UserEntity): Long

  @Update(onConflict = OnConflictStrategy.REPLACE)
  suspend fun update(entity: UserEntity)

  @Transaction
  suspend fun upsert(entity: UserEntity) {
    val id = insert(entity)
    if (id == -1L) {
      update(entity)
    }
  }
}