package com.example.shelfieapp.features.auth.domain.usecase

import com.example.shelfieapp.features.auth.domain.model.LoginRequest
import com.example.shelfieapp.features.auth.domain.model.User
import com.example.shelfieapp.features.auth.domain.repository.AuthRepository

class LoginUseCase(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(loginRequest: LoginRequest): Result<User> {
        return authRepository.login(loginRequest)
    }
}