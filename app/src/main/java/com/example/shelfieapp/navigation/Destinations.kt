package com.example.shelfieapp.navigation

sealed class Destinations(val route: String) {
    object Login : Destinations("login")
    object Home : Destinations("home")
    // Puedes agregar más destinos aquí después
    // object Profile : Destinations("profile")
    // object Settings : Destinations("settings")
}