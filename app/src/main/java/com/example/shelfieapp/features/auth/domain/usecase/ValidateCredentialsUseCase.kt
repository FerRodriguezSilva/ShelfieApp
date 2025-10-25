package com.example.shelfieapp.features.auth.domain.usecase

class ValidateCredentialsUseCase {
    operator fun invoke(email: String, password: String): Boolean {
        return email.isNotBlank() &&
                password.length >= 6 &&
                android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}