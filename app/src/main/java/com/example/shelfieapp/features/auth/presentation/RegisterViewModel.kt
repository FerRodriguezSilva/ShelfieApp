// package com.example.shelfieapp.features.auth.presentation

package com.example.shelfieapp.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelfieapp.features.auth.domain.model.RegisterRequest
import com.example.shelfieapp.features.auth.domain.usecase.RegisterUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class RegisterViewModel(
    private val registerUseCase: RegisterUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(RegisterState())
    val state: StateFlow<RegisterState> = _state.asStateFlow()

    fun onNameChange(name: String) {
        _state.update { it.copy(name = name, error = null) }
    }

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password, error = null) }
    }

    fun onConfirmPasswordChange(confirmPassword: String) {
        _state.update { it.copy(confirmPassword = confirmPassword, error = null) }
    }

    fun register() {
        // Validaciones básicas
        if (_state.value.name.isBlank()) {
            _state.update { it.copy(error = "El nombre es requerido") }
            return
        }

        if (_state.value.email.isBlank()) {
            _state.update { it.copy(error = "El email es requerido") }
            return
        }

        if (_state.value.password.isBlank()) {
            _state.update { it.copy(error = "La contraseña es requerida") }
            return
        }

        if (_state.value.password != _state.value.confirmPassword) {
            _state.update { it.copy(error = "Las contraseñas no coinciden") }
            return
        }

        if (_state.value.password.length < 6) {
            _state.update { it.copy(error = "La contraseña debe tener al menos 6 caracteres") }
            return
        }

        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = registerUseCase(
                RegisterRequest(
                    name = _state.value.name,
                    email = _state.value.email,
                    password = _state.value.password
                )
            )

            result.fold(
                onSuccess = { user ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isRegisterSuccessful = true
                        )
                    }
                },
                onFailure = { error ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Error al registrar usuario"
                        )
                    }
                }
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    fun clearState() {
        _state.update { RegisterState() }
    }
}