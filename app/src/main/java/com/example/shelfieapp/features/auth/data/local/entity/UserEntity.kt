package com.example.shelfieapp.features.auth.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val email: String,
    val name: String,
    val passwordHash: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isLoggedIn: Boolean = false
)