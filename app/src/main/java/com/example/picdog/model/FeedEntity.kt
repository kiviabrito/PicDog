package com.example.picdog.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class FeedEntity(
  @PrimaryKey
  val category: String,
  val list: List<String>
)