// Archivo: com/example/shelfieapp/features/recipes/domain/usecase/GetAvailableRecipesUseCase.kt
package com.example.shelfieapp.features.recipes.domain.usecase

import com.example.shelfieapp.features.pantry.domain.model.PantryItem
import com.example.shelfieapp.features.recipes.domain.repository.RecipeRepository
import com.example.shelfieapp.features.recipes.domain.service.RecipeMatchingService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine

class GetAvailableRecipesUseCase(
    private val repository: RecipeRepository,
    private val matchingService: RecipeMatchingService
) {
    operator fun invoke(pantryItems: List<PantryItem>): Flow<List<com.example.shelfieapp.features.recipes.domain.model.Recipe>> {
        return repository.getAllRecipes().combine(repository.getAllRecipes()) { allRecipes, _ ->
            matchingService.getAvailableRecipes(allRecipes, pantryItems)
        }
    }
}