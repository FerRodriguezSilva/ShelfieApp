package com.example.shelfieapp.features.auth.data.repository

import com.example.shelfieapp.features.auth.domain.model.LoginRequest
import com.example.shelfieapp.features.auth.domain.model.User
import com.example.shelfieapp.features.auth.domain.repository.AuthRepository

class AuthRepositoryImpl : AuthRepository {
    override suspend fun login(loginRequest: LoginRequest): Result<User> {
        // Simular llamada a API
        return try {
            kotlinx.coroutines.delay(1000) // Simular delay de red

            if (loginRequest.email == "test@test.com" && loginRequest.password == "123456") {
                Result.success(
                    User(
                        id = "1",
                        email = loginRequest.email,
                        name = "Usuario Test",
                        password = " "
                    )
                )
            } else {
                Result.failure(Exception("Credenciales inválidas"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        // Verificar si hay sesión activa
        return false
    }
}