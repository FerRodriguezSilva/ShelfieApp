// Archivo: com/example/shelfieapp/features/recipes/data/local/database/RecipeDatabase.kt
package com.example.shelfieapp.features.recipes.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.shelfieapp.features.recipes.data.local.dao.RecipeDao
import com.example.shelfieapp.features.recipes.data.local.entity.RecipeEntity

@Database(
    entities = [RecipeEntity::class],
    version = 1,
    exportSchema = false
)
abstract class RecipeDatabase : RoomDatabase() {
    abstract fun recipeDao(): RecipeDao

    companion object {
        const val DATABASE_NAME = "recipe_database"
    }
}