// Archivo: com/example/shelfieapp/features/recipes/data/local/entity/RecipeEntity.kt
package com.example.shelfieapp.features.recipes.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val instructions: String,
    val preparationTime: Int,
    val ingredientsJson: String, // Lista de ingredientes en JSON
    val isFavorite: Boolean,
    val createdAt: Long,
    val updatedAt: Long
)