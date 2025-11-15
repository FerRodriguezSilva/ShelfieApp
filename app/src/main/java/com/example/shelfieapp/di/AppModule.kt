package com.example.shelfieapp.di

import com.example.shelfieapp.features.auth.data.repository.AuthRepositoryImpl
import com.example.shelfieapp.features.auth.domain.repository.AuthRepository
import com.example.shelfieapp.features.auth.domain.usecase.LoginUseCase
import com.example.shelfieapp.features.auth.domain.usecase.ValidateCredentialsUseCase
import com.example.shelfieapp.features.auth.presentation.LoginViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // 🔥 Firebase
    single { FirebaseAuth.getInstance() }
    single { FirebaseDatabase.getInstance() }

    // Repository
    single<AuthRepository> {
        AuthRepositoryImpl(get(), get())
    }

    // Use Cases
    factory { LoginUseCase(get()) }
    factory { ValidateCredentialsUseCase() }

    // ViewModels
    viewModel { LoginViewModel(get(), get()) }
}