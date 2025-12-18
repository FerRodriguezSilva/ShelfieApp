package com.example.shelfieapp.features.auth.domain.usecase

import com.example.shelfieapp.features.auth.domain.model.RegisterRequest
import com.example.shelfieapp.features.auth.domain.model.User
import com.example.shelfieapp.features.auth.domain.repository.AuthRepository

class RegisterUseCase(
    private val repository: AuthRepository
) {
    suspend operator fun invoke(registerRequest: RegisterRequest): Result<User> {
        // Validaciones
        if (registerRequest.email.isBlank()) {
            return Result.failure(Exception("El email no puede estar vacío"))
        }
        if (registerRequest.password.length < 6) {
            return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
        }
        if (registerRequest.name.isBlank()) {
            return Result.failure(Exception("El nombre no puede estar vacío"))
        }

        return repository.register(registerRequest)
    }
}