// Archivo: com/example/shelfieapp/features/recipes/domain/usecase/GetAllRecipesUseCase.kt
package com.example.shelfieapp.features.recipes.domain.usecase

import com.example.shelfieapp.features.recipes.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow

class GetAllRecipesUseCase(
    private val repository: RecipeRepository
) {
    operator fun invoke(): Flow<List<com.example.shelfieapp.features.recipes.domain.model.Recipe>> {
        return repository.getAllRecipes()
    }
}