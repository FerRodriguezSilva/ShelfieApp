package com.example.shelfieapp.navigation

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.shelfieapp.features.auth.presentation.LoginScreen
import com.example.shelfieapp.features.home.presentation.HomeScreen

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
            // RecipesScreen() - Para implementar después
            Text("Recipes Screen - En construcción")
        }

        composable(Destinations.Shopping.route) {
            // ShoppingScreen() - Para implementar después
            Text("Shopping Screen - En construcción")
        }

        composable(Destinations.Settings.route) {
            // SettingsScreen() - Para implementar después
            Text("Settings Screen - En construcción")
        }
    }
}