package com.example.shelfieapp.navigation

sealed class Destinations(val route: String) {
    object Login : Destinations("login")
    object Home : Destinations("home")
    object Pantry : Destinations("pantry")
    object Recipes : Destinations("recipes")
    object Shopping : Destinations("shopping")
    object Settings : Destinations("settings")
    // Puedes agregar más destinos aquí después
    // object Profile : Destinations("profile")
    // object Settings : Destinations("settings")
}