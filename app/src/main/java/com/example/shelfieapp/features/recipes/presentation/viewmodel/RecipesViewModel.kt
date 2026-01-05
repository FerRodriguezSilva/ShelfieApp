// Archivo: com/example/shelfieapp/features/recipes/presentation/viewmodel/RecipesViewModel.kt
package com.example.shelfieapp.features.recipes.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelfieapp.features.auth.domain.repository.AuthRepository
import com.example.shelfieapp.features.pantry.domain.repository.PantryRepository
import com.example.shelfieapp.features.recipes.domain.service.RecipeMatchingService
import com.example.shelfieapp.features.recipes.domain.usecase.GetAllRecipesUseCase
import com.example.shelfieapp.features.recipes.domain.usecase.SearchRecipesUseCase
import com.example.shelfieapp.features.recipes.presentation.state.RecipeState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RecipesViewModel(
    private val getAllRecipesUseCase: GetAllRecipesUseCase,
    private val searchRecipesUseCase: SearchRecipesUseCase,
    private val pantryRepository: PantryRepository,
    private val authRepository: AuthRepository,
    private val recipeMatchingService: RecipeMatchingService
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
                    // Obtener recetas
                    getAllRecipesUseCase().collect { recipes ->
                        // Obtener ingredientes de la despensa
                        pantryRepository.getPantryItems(currentUser.id).collect { pantryItems ->
                            // Filtrar recetas disponibles
                            val availableRecipes = recipeMatchingService.getAvailableRecipes(recipes, pantryItems)
                            val possibleRecipes = recipeMatchingService.getPossibleRecipes(recipes, pantryItems)

                            _state.update {
                                it.copy(
                                    allRecipes = recipes,
                                    availableRecipes = availableRecipes,
                                    possibleRecipes = possibleRecipes,
                                    isLoading = false,
                                    error = null
                                )
                            }
                        }
                    }
                } else {
                    // Si no hay usuario, mostrar todas las recetas
                    getAllRecipesUseCase().collect { recipes ->
                        _state.update {
                            it.copy(
                                allRecipes = recipes,
                                availableRecipes = emptyList(),
                                possibleRecipes = emptyList(),
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

    fun searchRecipes(query: String) {
        if (query.isBlank()) {
            loadRecipes()
            return
        }

        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, searchQuery = query) }

            try {
                val currentUser = authRepository.getLoggedInUser()

                searchRecipesUseCase(query).collect { recipes ->
                    if (currentUser != null) {
                        pantryRepository.getPantryItems(currentUser.id).collect { pantryItems ->
                            val availableRecipes = recipeMatchingService.getAvailableRecipes(recipes, pantryItems)
                            val possibleRecipes = recipeMatchingService.getPossibleRecipes(recipes, pantryItems)

                            _state.update {
                                it.copy(
                                    allRecipes = recipes,
                                    availableRecipes = availableRecipes,
                                    possibleRecipes = possibleRecipes,
                                    isLoading = false
                                )
                            }
                        }
                    } else {
                        _state.update {
                            it.copy(
                                allRecipes = recipes,
                                availableRecipes = emptyList(),
                                possibleRecipes = emptyList(),
                                isLoading = false
                            )
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

    fun toggleFavorite(recipe: com.example.shelfieapp.features.recipes.domain.model.Recipe) {
        viewModelScope.launch {
            try {
                // Actualizar en la UI primero para feedback inmediato
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

                // TODO: Actualizar en el repositorio cuando tengas esa funcionalidad
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

    fun refresh() {
        loadRecipes()
    }
}