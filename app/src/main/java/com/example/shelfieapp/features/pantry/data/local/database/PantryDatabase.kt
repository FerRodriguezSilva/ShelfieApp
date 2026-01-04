package com.example.shelfieapp.features.pantry.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.shelfieapp.features.pantry.data.local.dao.PantryDao
import com.example.shelfieapp.features.pantry.data.local.entity.PantryItemEntity

@Database(
    entities = [PantryItemEntity::class],
    version = 2,
    exportSchema = false
)
abstract class PantryDatabase : RoomDatabase() {
    abstract fun pantryDao(): PantryDao

    companion object {
        const val DATABASE_NAME = "pantry_database"
    }
}