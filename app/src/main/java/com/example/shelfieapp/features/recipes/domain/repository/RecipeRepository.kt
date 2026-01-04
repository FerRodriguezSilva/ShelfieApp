// Archivo: com/example/shelfieapp/features/recipes/domain/repository/RecipeRepository.kt
package com.example.shelfieapp.features.recipes.domain.repository

import com.example.shelfieapp.features.recipes.domain.model.Recipe
import kotlinx.coroutines.flow.Flow

interface RecipeRepository {
    fun getAllRecipes(): Flow<List<Recipe>>
    fun getRecipeById(recipeId: String): Flow<Recipe?>
    fun getFavoriteRecipes(): Flow<List<Recipe>>
    fun searchRecipes(query: String): Flow<List<Recipe>>

    suspend fun syncRecipes(): Result<Unit>
    suspend fun updateFavoriteStatus(recipeId: String, isFavorite: Boolean): Result<Unit>
}