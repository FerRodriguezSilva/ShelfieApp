package com.example.shelfieapp.features.recipes.domain.model

data class Recipe(
    val id: String = "",
    val nombre: String = "",
    val dificultad: Difficulty = Difficulty.MEDIA,
    val tiempo: Int = 0, // minutos
    val categoria: String = "",
    val ingredientes: List<RecipeIngredient> = emptyList(),
    val instrucciones: String = "",
    val imagen: String = ""
)

data class RecipeIngredient(
    val nombre: String = "",
    val cantidad: Double = 0.0,
    val unidad: String = ""
)

enum class Difficulty {
    FACIL, MEDIA, DIFICIL
}