package com.example.shelfieapp.features.auth.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.shelfieapp.features.auth.data.local.dao.UserDao
import com.example.shelfieapp.features.auth.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
}