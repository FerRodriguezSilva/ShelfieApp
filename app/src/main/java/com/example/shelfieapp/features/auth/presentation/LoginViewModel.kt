// package com.example.shelfieapp.features.auth.presentation

package com.example.shelfieapp.features.auth.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.shelfieapp.features.auth.domain.model.LoginRequest
import com.example.shelfieapp.features.auth.domain.usecase.LoginUseCase
import com.example.shelfieapp.features.auth.domain.usecase.ValidateCredentialsUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val loginUseCase: LoginUseCase,
    private val validateCredentialsUseCase: ValidateCredentialsUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginState())
    val state: StateFlow<LoginState> = _state.asStateFlow()

    fun login() {
        val email = _state.value.email.trim()
        val password = _state.value.password

        // Validaciones básicas
        if (email.isEmpty() || password.isEmpty()) {
            _state.update {
                it.copy(error = "Por favor, completa todos los campos")
            }
            return
        }

        // Validar formato de email
        if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _state.update {
                it.copy(error = "Por favor, ingresa un email válido")
            }
            return
        }

        // Validar longitud de contraseña
        if (password.length < 6) {
            _state.update {
                it.copy(error = "La contraseña debe tener al menos 6 caracteres")
            }
            return
        }

        _state.update { it.copy(isLoading = true, error = null) }

        viewModelScope.launch {
            val result = loginUseCase(
                LoginRequest(email = email, password = password)
            )

            result.fold(
                onSuccess = { user ->
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isLoginSuccessful = true
                        )
                    }
                },
                onFailure = { error ->
                    val errorMessage = when {
                        error.message?.contains("no encontrado", ignoreCase = true) == true ->
                            "Usuario no encontrado"
                        error.message?.contains("contraseña incorrecta", ignoreCase = true) == true ->
                            "Contraseña incorrecta"
                        error.message?.contains("conexión", ignoreCase = true) == true ->
                            "Error de conexión. Intenta en modo offline"
                        error.message?.contains("offline", ignoreCase = true) == true ->
                            "Modo offline activado. Usando datos locales"
                        else -> error.message ?: "Error al iniciar sesión"
                    }

                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = errorMessage
                        )
                    }
                }
            )
        }
    }

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password, error = null) }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}