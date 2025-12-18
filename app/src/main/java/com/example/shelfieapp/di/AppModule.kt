package com.example.shelfieapp.di

import androidx.room.Room
import com.example.shelfieapp.features.auth.data.local.database.AppDatabase
import com.example.shelfieapp.features.auth.data.repository.AuthRepositoryImpl
import com.example.shelfieapp.features.auth.domain.repository.AuthRepository
import com.example.shelfieapp.features.auth.domain.usecase.LoginUseCase
import com.example.shelfieapp.features.auth.domain.usecase.RegisterUseCase
import com.example.shelfieapp.features.auth.domain.usecase.ValidateCredentialsUseCase
import com.example.shelfieapp.features.auth.presentation.LoginViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Database
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "shelfie_database"
        ).build()
    }

    // DAOs
    single { get<AppDatabase>().userDao() }

    // Repository
    single<AuthRepository> { AuthRepositoryImpl(get()) }

    // Use Cases
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { ValidateCredentialsUseCase() }

    // ViewModels
    viewModel { LoginViewModel(get(), get()) }
}