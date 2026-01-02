package com.example.shelfieapp.features.pantry.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelfieapp.features.auth.domain.repository.AuthRepository // Usa la interfaz, no AuthRepositoryImpl
import com.example.shelfieapp.features.pantry.domain.model.PantryItem
import com.example.shelfieapp.features.pantry.domain.usecase.*
import com.example.shelfieapp.features.pantry.presentation.state.PantryState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PantryViewModel(
    private val addPantryItemUseCase: AddPantryItemUseCase,
    private val getPantryItemsUseCase: GetPantryItemsUseCase,
    private val updatePantryItemUseCase: UpdatePantryItemUseCase,
    private val deletePantryItemUseCase: DeletePantryItemUseCase,
    private val authRepository: AuthRepository // Interfaz, no implementación
) : ViewModel() {

    private val _state = MutableStateFlow(PantryState())
    val state: StateFlow<PantryState> = _state.asStateFlow()

    init {
        loadPantryItems()
    }

    private fun loadPantryItems() {
        viewModelScope.launch {
            val currentUser = authRepository.getLoggedInUser()
            if (currentUser != null) {
                getPantryItemsUseCase(currentUser.id).collect { items ->
                    _state.update { currentState ->
                        currentState.copy(items = items)
                    }
                }
            }
        }
    }

    fun addItem(name: String, quantity: Double, unit: String, category: String?) {
        viewModelScope.launch {
            val currentUser = authRepository.getLoggedInUser()
            if (currentUser != null) {
                _state.update { currentState ->
                    currentState.copy(isLoading = true)
                }

                val newItem = PantryItem(
                    userId = currentUser.id,
                    name = name,
                    quantity = quantity,
                    unit = unit,
                    category = category
                )

                val result = addPantryItemUseCase(newItem)
                result.fold(
                    onSuccess = {
                        _state.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                error = null
                            )
                        }
                    },
                    onFailure = { error ->
                        _state.update { currentState ->
                            currentState.copy(
                                isLoading = false,
                                error = error.message ?: "Error al agregar"
                            )
                        }
                    }
                )
            }
        }
    }

    fun updateItem(item: PantryItem) {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(isLoading = true)
            }

            val result = updatePantryItemUseCase(item)
            result.fold(
                onSuccess = {
                    _state.update { currentState ->
                        currentState.copy(isLoading = false)
                    }
                },
                onFailure = { error ->
                    _state.update { currentState ->
                        currentState.copy(isLoading = false, error = error.message)
                    }
                }
            )
        }
    }

    fun deleteItem(itemId: String) {
        viewModelScope.launch {
            _state.update { currentState ->
                currentState.copy(isLoading = true)
            }

            val result = deletePantryItemUseCase(itemId)
            result.fold(
                onSuccess = {
                    _state.update { currentState ->
                        currentState.copy(isLoading = false)
                    }
                },
                onFailure = { error ->
                    _state.update { currentState ->
                        currentState.copy(isLoading = false, error = error.message)
                    }
                }
            )
        }
    }

    fun clearError() {
        _state.update { currentState ->
            currentState.copy(error = null)
        }
    }
}