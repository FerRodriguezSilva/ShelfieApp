// Archivo: com/example/shelfieapp/features/recipes/domain/usecase/GetRecipeByIdUseCase.kt
package com.example.shelfieapp.features.recipes.domain.usecase

import com.example.shelfieapp.features.recipes.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow

class GetRecipeByIdUseCase(
    private val repository: RecipeRepository
) {
    operator fun invoke(recipeId: String): Flow<com.example.shelfieapp.features.recipes.domain.model.Recipe?> {
        return repository.getRecipeById(recipeId)
    }
}