package com.example.shelfieapp.di

import androidx.room.Room
import com.example.shelfieapp.features.auth.data.local.database.AppDatabase
import com.example.shelfieapp.features.auth.data.remote.FirebaseRealtimeDataSource
import com.example.shelfieapp.features.auth.data.repository.AuthRepositoryImpl
import com.example.shelfieapp.features.auth.domain.repository.AuthRepository
import com.example.shelfieapp.features.auth.domain.usecase.LoginUseCase
import com.example.shelfieapp.features.auth.domain.usecase.RegisterUseCase
import com.example.shelfieapp.features.auth.domain.usecase.ValidateCredentialsUseCase
import com.example.shelfieapp.features.auth.presentation.LoginViewModel
import com.example.shelfieapp.features.auth.presentation.RegisterViewModel
import com.example.shelfieapp.features.pantry.data.local.database.PantryDatabase
import com.example.shelfieapp.features.pantry.data.remote.FirebasePantryDataSource
import com.example.shelfieapp.features.pantry.data.repository.PantryRepositoryImpl
import com.example.shelfieapp.features.pantry.domain.repository.PantryRepository
import com.example.shelfieapp.features.pantry.domain.usecase.AddPantryItemUseCase
import com.example.shelfieapp.features.pantry.domain.usecase.DeletePantryItemUseCase
import com.example.shelfieapp.features.pantry.domain.usecase.GetPantryItemsUseCase
import com.example.shelfieapp.features.pantry.domain.usecase.UpdatePantryItemUseCase
import com.example.shelfieapp.features.pantry.presentation.viewmodel.PantryViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val appModule = module {
    // Database Room para usuarios
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "shelfie_database"
        ).build()
    }

    // Database Room para despensa
    single {
        Room.databaseBuilder(
            androidContext(),
            PantryDatabase::class.java,
            PantryDatabase.DATABASE_NAME
        ).build()
    }

    // DAOs
    single { get<AppDatabase>().userDao() }
    single { get<PantryDatabase>().pantryDao() }

    // Firebase Data Sources
    single { FirebaseRealtimeDataSource() }
    single { FirebasePantryDataSource() }

    // Repositories
    single<AuthRepository> {
        AuthRepositoryImpl(
            userDao = get(),
            firebaseDataSource = get()
        )
    }

    single<PantryRepository> {
        PantryRepositoryImpl(
            pantryDao = get(),
            firebaseDataSource = get(),
            authRepository = get<AuthRepository>() // Usa la interfaz, no la implementación
        )
    }

    // Use Cases para Auth
    factory { LoginUseCase(get()) }
    factory { RegisterUseCase(get()) }
    factory { ValidateCredentialsUseCase() }

    // Use Cases para Pantry
    factory { AddPantryItemUseCase(get<PantryRepository>()) }
    factory { GetPantryItemsUseCase(get<PantryRepository>()) }
    factory { UpdatePantryItemUseCase(get<PantryRepository>()) }
    factory { DeletePantryItemUseCase(get<PantryRepository>()) }

    // ViewModels
    viewModel { LoginViewModel(get(), get()) }
    viewModel { RegisterViewModel(get()) }
    viewModel {
        PantryViewModel(
            addPantryItemUseCase = get(),
            getPantryItemsUseCase = get(),
            updatePantryItemUseCase = get(),
            deletePantryItemUseCase = get(),
            authRepository = get() // Aquí usa AuthRepository (interfaz)
        )
    }
}