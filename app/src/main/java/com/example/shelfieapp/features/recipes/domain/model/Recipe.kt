// Archivo: com/example/shelfieapp/features/recipes/domain/model/Recipe.kt
package com.example.shelfieapp.features.recipes.domain.model

import java.util.UUID

data class Recipe(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String,
    val instructions: String,
    val preparationTime: Int, // en minutos
    val ingredients: List<RecipeIngredient>,
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class RecipeIngredient(
    val name: String,
    val quantity: Double,
    val unit: String,
    val isOptional: Boolean = false
)