// Archivo: com/example/shelfieapp/features/recipes/presentation/state/RecipeState.kt
package com.example.shelfieapp.features.recipes.presentation.state

import com.example.shelfieapp.features.recipes.domain.model.Recipe

data class RecipeState(
    val allRecipes: List<Recipe> = emptyList(),
    val availableRecipes: List<Recipe> = emptyList(),
    val possibleRecipes: List<Recipe> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val searchQuery: String = ""
)