// Archivo: com/example/shelfieapp/di/AppModule.kt
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
import com.example.shelfieapp.features.pantry.domain.service.ExpirationNotificationService
import com.example.shelfieapp.features.pantry.domain.usecase.AddPantryItemUseCase
import com.example.shelfieapp.features.pantry.domain.usecase.DeletePantryItemUseCase
import com.example.shelfieapp.features.pantry.domain.usecase.GetPantryItemsUseCase
import com.example.shelfieapp.features.pantry.domain.usecase.UpdatePantryItemUseCase
import com.example.shelfieapp.features.pantry.presentation.viewmodel.PantryViewModel
import com.example.shelfieapp.features.recipes.data.local.database.RecipeDatabase
import com.example.shelfieapp.features.recipes.data.remote.FirebaseRecipeDataSource
import com.example.shelfieapp.features.recipes.data.repository.RecipeRepositoryImpl
import com.example.shelfieapp.features.recipes.domain.repository.RecipeRepository
import com.example.shelfieapp.features.recipes.domain.service.RecipeMatchingService
import com.example.shelfieapp.features.recipes.domain.usecase.GetAllRecipesUseCase
import com.example.shelfieapp.features.recipes.domain.usecase.GetAvailableRecipesUseCase
import com.example.shelfieapp.features.recipes.domain.usecase.GetRecipeByIdUseCase
import com.example.shelfieapp.features.recipes.domain.usecase.SearchRecipesUseCase
import com.example.shelfieapp.features.recipes.presentation.viewmodel.RecipesViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModelOf
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val appModule = module {
    // Database Room para usuarios
    single {
        Room.databaseBuilder(
            androidContext(),
            AppDatabase::class.java,
            "shelfie_database"
        ).fallbackToDestructiveMigration() // Agrega esto para desarrollo
            .build()
    }

    // Database Room para despensa
    single {
        Room.databaseBuilder(
            androidContext(),
            PantryDatabase::class.java,
            PantryDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration() // Agrega esto para desarrollo
            .build()
    }

    // DAOs
    single { get<AppDatabase>().userDao() }
    single { get<PantryDatabase>().pantryDao() }

    // Firebase Data Sources
    singleOf(::FirebaseRealtimeDataSource)
    singleOf(::FirebasePantryDataSource)

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
            authRepository = get()
        )
    }
    single {
        ExpirationNotificationService(
            context = androidContext(),
            pantryRepository = get()
        )
    }
    single {
        Room.databaseBuilder(
            androidContext(),
            RecipeDatabase::class.java,
            RecipeDatabase.DATABASE_NAME
        ).fallbackToDestructiveMigration()
            .build()
    }

    // DAO de recetas
    single { get<RecipeDatabase>().recipeDao() }

    // Firebase Data Sources
    singleOf(::FirebaseRecipeDataSource)

    // Repositories
    single<RecipeRepository> {
        RecipeRepositoryImpl(
            recipeDao = get(),
            firebaseDataSource = get()
        )
    }

    // Services
    single { RecipeMatchingService() }
    // Use Cases para Auth
    factoryOf(::LoginUseCase)
    factoryOf(::RegisterUseCase)
    factory { ValidateCredentialsUseCase() }

    // Use Cases para Pantry
    factoryOf(::AddPantryItemUseCase)
    factoryOf(::GetPantryItemsUseCase)
    factoryOf(::UpdatePantryItemUseCase)
    factoryOf(::DeletePantryItemUseCase)

    // ViewModels
    viewModelOf(::LoginViewModel)
    viewModelOf(::RegisterViewModel)
    viewModelOf(::PantryViewModel)

    // Use Cases para Recipes
    factoryOf(::GetAllRecipesUseCase)
    factoryOf(::GetAvailableRecipesUseCase)
    factoryOf(::GetRecipeByIdUseCase)
    factoryOf(::SearchRecipesUseCase)

    // ViewModels
    viewModelOf(::RecipesViewModel)
}