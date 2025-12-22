// package com.example.shelfieapp.features.auth.presentation

package com.example.shelfieapp.features.auth.presentation

data class RegisterState(
    val name: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val isLoading: Boolean = false,
    val error: String? = null,
    val isRegisterSuccessful: Boolean = false
)