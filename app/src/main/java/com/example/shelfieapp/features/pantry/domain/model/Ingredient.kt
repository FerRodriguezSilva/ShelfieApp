package com.example.shelfieapp.features.pantry.domain.model

data class Ingredient(
    val id: String = "",
    val nombre: String = "",
    val cantidad: Double = 0.0,
    val unidad: String = "",
    val categoria: IngredientCategory = IngredientCategory.OTROS,
    val fechaExpira: String = "", // ISO 8601: "2025-01-20"
    val alertaBaja: Boolean = false,
    val fechaAgregado: String = ""
)

enum class IngredientCategory {
    LACTEOS,
    FRUTAS,
    VERDURAS,
    CARNES,
    GRANOS,
    ENLATADOS,
    CONDIMENTOS,
    OTROS
}