package com.example.shelfieapp.features.auth.domain.usecase

import com.example.shelfieapp.features.auth.domain.model.User
import com.example.shelfieapp.features.auth.domain.repository.AuthRepository

class RegisterUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(
        email: String,
        password: String,
        nombre: String
    ): Result<User> {
        return authRepository.register(email, password, nombre)
    }

}