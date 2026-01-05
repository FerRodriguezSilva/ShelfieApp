// Archivo: com/example/shelfieapp/features/recipes/domain/usecase/SyncRecipesUseCase.kt
package com.example.shelfieapp.features.recipes.domain.usecase

import com.example.shelfieapp.features.recipes.domain.repository.RecipeRepository

class SyncRecipesUseCase(
    private val repository: RecipeRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return repository.syncRecipes()
    }
}