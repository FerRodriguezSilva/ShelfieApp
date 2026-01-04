// Archivo: com/example/shelfieapp/features/recipes/data/repository/RecipeRepositoryImpl.kt
package com.example.shelfieapp.features.recipes.data.repository

import com.example.shelfieapp.features.recipes.data.local.dao.RecipeDao
import com.example.shelfieapp.features.recipes.data.local.mapper.toDomainModel
import com.example.shelfieapp.features.recipes.data.local.mapper.toEntity
import com.example.shelfieapp.features.recipes.data.remote.FirebaseRecipeDataSource
import com.example.shelfieapp.features.recipes.domain.model.Recipe
import com.example.shelfieapp.features.recipes.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RecipeRepositoryImpl(
    private val recipeDao: RecipeDao,
    private val firebaseDataSource: FirebaseRecipeDataSource
) : RecipeRepository {

    override fun getAllRecipes(): Flow<List<Recipe>> {
        return recipeDao.getAllRecipes()
            .map { entities -> entities.map { it.toDomainModel() } }
    }

    override fun getRecipeById(recipeId: String): Flow<Recipe?> {
        return recipeDao.getAllRecipes()
            .map { entities ->
                entities.find { it.id == recipeId }?.toDomainModel()
            }
    }

    override fun getFavoriteRecipes(): Flow<List<Recipe>> {
        return recipeDao.getFavoriteRecipes()
            .map { entities -> entities.map { it.toDomainModel() } }
    }

    override fun searchRecipes(query: String): Flow<List<Recipe>> {
        return recipeDao.searchRecipes(query)
            .map { entities -> entities.map { it.toDomainModel() } }
    }

    override suspend fun syncRecipes(): Result<Unit> {
        return try {
            val remoteRecipes = firebaseDataSource.getAllRecipes().getOrDefault(emptyList())

            remoteRecipes.forEach { recipe ->
                recipeDao.insertRecipe(recipe.toEntity())
            }

            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updateFavoriteStatus(recipeId: String, isFavorite: Boolean): Result<Unit> {
        return try {
            recipeDao.updateFavoriteStatus(recipeId, isFavorite)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}