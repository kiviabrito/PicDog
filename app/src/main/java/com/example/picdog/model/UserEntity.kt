package com.example.picdog.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UserEntity(
    @PrimaryKey
    val _id: String,
    val token: String,
    val createdAt: String,
    val updatedAt: String,
    val __v: Int
)