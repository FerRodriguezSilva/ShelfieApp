// Archivo: com/example/shelfieapp/features/recipes/presentation/viewmodel/RecipesViewModel.kt
package com.example.shelfieapp.features.recipes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelfieapp.features.auth.domain.repository.AuthRepository
import com.example.shelfieapp.features.pantry.domain.model.PantryItem
import com.example.shelfieapp.features.pantry.domain.repository.PantryRepository
import com.example.shelfieapp.features.recipes.domain.model.Recipe
import com.example.shelfieapp.features.recipes.domain.service.RecipeMatchingService
import com.example.shelfieapp.features.recipes.domain.usecase.GetAllRecipesUseCase
import com.example.shelfieapp.features.recipes.domain.usecase.GetAvailableRecipesUseCase
import com.example.shelfieapp.features.recipes.domain.usecase.SearchRecipesUseCase
import com.example.shelfieapp.features.recipes.presentation.state.RecipeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecipesViewModel(
    private val getAllRecipesUseCase: GetAllRecipesUseCase,
    private val getAvailableRecipesUseCase: GetAvailableRecipesUseCase,
    private val searchRecipesUseCase: SearchRecipesUseCase,
    private val pantryRepository: PantryRepository,
    private val authRepository: AuthRepository,
    private val matchingService: RecipeMatchingService
) : ViewModel() {

    private val _state = MutableStateFlow(RecipeState())
    val state: StateFlow<RecipeState> = _state.asStateFlow()

    init {
        loadRecipes()
    }

    private fun loadRecipes() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val currentUser = authRepository.getLoggedInUser()
                if (currentUser != null) {
                    // Combinar flujos de recetas y despensa
                    getAllRecipesUseCase().combine(
                        pantryRepository.getPantryItems(currentUser.id)
                    ) { recipes, pantryItems ->
                        updateRecipesLists(recipes, pantryItems)
                    }.collect { (allRecipes, availableRecipes, possibleRecipes) ->
                        _state.update {
                            it.copy(
                                allRecipes = allRecipes,
                                availableRecipes = availableRecipes,
                                possibleRecipes = possibleRecipes,
                                isLoading = false,
                                error = null
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        isLoading = false,
                        error = "Error al cargar recetas: ${e.message}"
                    )
                }
            }
        }
    }

    private fun updateRecipesLists(
        recipes: List<Recipe>,
        pantryItems: List<PantryItem>
    ): Triple<List<Recipe>, List<Recipe>, List<Recipe>> {
        val availableRecipes = matchingService.getAvailableRecipes(recipes, pantryItems)
        val possibleRecipes = matchingService.getPossibleRecipes(recipes, pantryItems)

        return Triple(recipes, availableRecipes, possibleRecipes)
    }

    fun searchRecipes(query: String) {
        _state.update { it.copy(searchQuery = query) }

        if (query.isBlank()) {
            loadRecipes()
        } else {
            viewModelScope.launch {
                try {
                    searchRecipesUseCase(query).collect { recipes ->
                        val currentUser = authRepository.getLoggedInUser()
                        if (currentUser != null) {
                            pantryRepository.getPantryItems(currentUser.id).collect { pantryItems ->
                                val (_, availableRecipes, possibleRecipes) =
                                    updateRecipesLists(recipes, pantryItems)

                                _state.update {
                                    it.copy(
                                        allRecipes = recipes,
                                        availableRecipes = availableRecipes,
                                        possibleRecipes = possibleRecipes,
                                        isLoading = false
                                    )
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    _state.update {
                        it.copy(
                            error = "Error al buscar recetas: ${e.message}",
                            isLoading = false
                        )
                    }
                }
            }
        }
    }

    fun toggleFavorite(recipe: Recipe) {
        viewModelScope.launch {
            try {
                // Aquí implementarías la lógica para actualizar el estado de favorito
                // Por ahora solo actualizamos el estado local
                val updatedRecipes = _state.value.allRecipes.map {
                    if (it.id == recipe.id) {
                        it.copy(isFavorite = !it.isFavorite)
                    } else {
                        it
                    }
                }

                _state.update {
                    it.copy(allRecipes = updatedRecipes)
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(error = "Error al actualizar favorito: ${e.message}")
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}