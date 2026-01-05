// Archivo: com/example/shelfieapp/features/home/presentation/viewmodel/HomeViewModel.kt
package com.example.shelfieapp.features.home.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelfieapp.features.auth.domain.repository.AuthRepository
import com.example.shelfieapp.features.pantry.domain.repository.PantryRepository
import com.example.shelfieapp.features.recipes.domain.repository.RecipeRepository
import com.example.shelfieapp.features.recipes.domain.service.RecipeMatchingService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class HomeState(
    val username: String = "",
    val totalIngredients: Int = 0,
    val lowStockItems: Int = 0,
    val suggestedRecipes: List<String> = emptyList(),
    val canMakeRecipes: List<String> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

class HomeViewModel(
    private val authRepository: AuthRepository,
    private val pantryRepository: PantryRepository,
    private val recipeRepository: RecipeRepository,
    private val recipeMatchingService: RecipeMatchingService
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    init {
        loadHomeData()
    }

    private fun loadHomeData() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }

            try {
                val currentUser = authRepository.getLoggedInUser()
                if (currentUser != null) {
                    // Obtener nombre de usuario
                    _state.update { it.copy(username = currentUser.name ?: "Usuario") }

                    // Obtener datos de la despensa
                    val pantryItems = pantryRepository.getPantryItems(currentUser.id).firstOrNull() ?: emptyList()
                    val totalItems = pantryItems.size
                    val lowStock = pantryItems.count { it.quantity <= 2.0 } // Considerar bajo stock si tiene 2 o menos

                    _state.update { it.copy(totalIngredients = totalItems, lowStockItems = lowStock) }

                    // Obtener recetas sugeridas
                    val allRecipes = recipeRepository.getAllRecipes().firstOrNull() ?: emptyList()
                    val availableRecipes = recipeMatchingService.getAvailableRecipes(allRecipes, pantryItems)

                    val canMakeRecipes = availableRecipes.take(3).map { it.name }
                    val suggestedRecipes = allRecipes.take(3).map { it.name }

                    _state.update {
                        it.copy(
                            canMakeRecipes = canMakeRecipes,
                            suggestedRecipes = suggestedRecipes,
                            isLoading = false
                        )
                    }
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(
                        error = "Error al cargar datos: ${e.message}",
                        isLoading = false
                    )
                }
            }
        }
    }

    fun refresh() {
        loadHomeData()
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}