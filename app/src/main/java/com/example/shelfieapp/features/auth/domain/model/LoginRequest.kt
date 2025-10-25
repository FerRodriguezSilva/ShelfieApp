package com.example.shelfieapp.features.auth.domain.model

data class LoginRequest(
    val email: String,
    val password: String
)