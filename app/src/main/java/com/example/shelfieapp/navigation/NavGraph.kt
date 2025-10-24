package com.example.shelfieapp.navigation

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
                    // Navegar al home y limpiar el back stack
                    navController.navigate(Destinations.Home.route) {
                        popUpTo(Destinations.Login.route) { inclusive = true }
                    }
                }
            )
        }

        composable(Destinations.Home.route) {
            HomeScreen()
        }
    }
}