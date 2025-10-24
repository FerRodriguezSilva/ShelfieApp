package com.example.shelfieapp.di

import com.example.shelfieapp.features.auth.data.repository.AuthRepositoryImpl
import com.example.shelfieapp.features.auth.domain.repository.AuthRepository
import com.example.shelfieapp.features.auth.domain.usecase.LoginUseCase
import com.example.shelfieapp.features.auth.domain.usecase.ValidateCredentialsUseCase
import com.example.shelfieapp.features.auth.presentation.LoginViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Repository
    single<AuthRepository> { AuthRepositoryImpl() }

    // Use Cases
    factory { LoginUseCase(get()) }
    factory { ValidateCredentialsUseCase() }

    // ViewModels
    viewModel { LoginViewModel(get(), get()) }
}