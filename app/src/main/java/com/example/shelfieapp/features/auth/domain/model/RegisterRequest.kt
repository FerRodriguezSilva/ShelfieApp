package com.example.shelfieapp.features.auth.domain.model

data class RegisterRequest(
    val email: String,
    val password: String,
    val name: String
)