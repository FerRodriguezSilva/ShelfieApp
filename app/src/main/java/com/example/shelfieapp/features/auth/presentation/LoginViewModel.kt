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
    init{
        println("LoginViewModel Inicializado")
    }

    fun onEmailChange(email: String) {
        _state.update { it.copy(email = email, error = null) }
    }

    fun onPasswordChange(password: String) {
        _state.update { it.copy(password = password, error = null) }
    }

    fun login() {
        println("🔥 Login iniciado: ${_state.value.email}")
        // Validar credenciales primero
        if (!validateCredentialsUseCase(_state.value.email, _state.value.password)) {
            _state.update {
                it.copy(
                    error = "Email inválido o contraseña muy corta (mínimo 6 caracteres)"
                )
            }
            return
        }

        _state.update { it.copy(isLoading = true, error = null) }
        println("✅ Validación exitosa, haciendo login...")

        viewModelScope.launch {
            val result = loginUseCase(
                LoginRequest(
                    email = _state.value.email,
                    password = _state.value.password
                )
            )

            result.fold(
                onSuccess = { user ->
                    println("🎉 Login exitoso: $user")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            isLoginSuccessful = true
                        )
                    }
                },
                onFailure = { error ->
                    println("❌ Login falló: ${error.message}")
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.message ?: "Error desconocido"
                        )
                    }
                }
            )
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}