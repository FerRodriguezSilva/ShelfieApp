package com.example.shelfieapp.features.auth.domain.repository

import com.example.shelfieapp.features.auth.domain.model.LoginRequest
import com.example.shelfieapp.features.auth.domain.model.User

interface AuthRepository {
    suspend fun login(loginRequest: LoginRequest): Result<User>
    suspend fun isLoggedIn(): Boolean
}