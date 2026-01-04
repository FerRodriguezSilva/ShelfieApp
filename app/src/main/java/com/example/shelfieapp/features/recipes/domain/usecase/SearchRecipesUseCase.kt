// Archivo: com/example/shelfieapp/features/recipes/domain/usecase/SearchRecipesUseCase.kt
package com.example.shelfieapp.features.recipes.domain.usecase

import com.example.shelfieapp.features.recipes.domain.repository.RecipeRepository
import kotlinx.coroutines.flow.Flow

class SearchRecipesUseCase(
    private val repository: RecipeRepository
) {
    operator fun invoke(query: String): Flow<List<com.example.shelfieapp.features.recipes.domain.model.Recipe>> {
        return repository.searchRecipes(query)
    }
}