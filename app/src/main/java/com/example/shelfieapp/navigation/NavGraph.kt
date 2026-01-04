package com.example.shelfieapp.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shelfieapp.features.auth.presentation.LoginScreen
import com.example.shelfieapp.features.auth.presentation.RegisterScreen
import com.example.shelfieapp.features.home.presentation.HomeScreen
import com.example.shelfieapp.features.pantry.presentation.screens.PantryScreen
import com.example.shelfieapp.features.recipes.presentation.screens.RecipesScreen

@Composable
fun NavGraph() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Destinations.Login.route
    ) {
        composable(Destinations.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Destinations.Home.route) {
                        popUpTo(Destinations.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Destinations.Register.route)
                }
            )
        }
        composable(Destinations.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Destinations.Home.route) {
                        popUpTo(Destinations.Login.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Destinations.Login.route) {
                        popUpTo(Destinations.Register.route)
                    }
                }
            )
        }

        composable(Destinations.Home.route) {
            HomeScreen(
                onNavigate = { route ->
                    when (route) {
                        "pantry" -> navController.navigate(Destinations.Pantry.route)
                        "recipes" -> navController.navigate(Destinations.Recipes.route)
                        "shopping" -> navController.navigate(Destinations.Shopping.route)
                        "settings" -> navController.navigate(Destinations.Settings.route)
                    }
                }
            )
        }

        composable(Destinations.Pantry.route) {
            // PantryScreen() - Para implementar después
            Text("Pantry Screen - En construcción")
        }

        composable(Destinations.Recipes.route) {
            RecipesScreen(
                onBack = { navController.popBackStack() },
                onRecipeClick = { recipeId ->
                    // TODO: Navegar a pantalla de detalle de receta
                }
            )
        }

        composable(Destinations.Shopping.route) {
            // ShoppingScreen() - Para implementar después
            Text("Shopping Screen - En construcción")
        }

        composable(Destinations.Settings.route) {
            // SettingsScreen() - Para implementar después
            Text("Settings Screen - En construcción")
        }
        composable(Destinations.Pantry.route) {
            PantryScreen(
                onBack = { navController.popBackStack() }
            )
        }
    }
}