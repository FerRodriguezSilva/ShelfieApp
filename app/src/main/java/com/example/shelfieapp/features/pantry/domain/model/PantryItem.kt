package com.example.shelfieapp.features.pantry.domain.model

import java.util.UUID

data class PantryItem(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,  // Para vincular con el usuario
    val name: String,
    val quantity: Double,
    val unit: String,  // gramos, litros, unidades, etc.
    val category: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true
) {
    fun displayQuantity(): String {
        return if (quantity % 1 == 0.0) {
            "${quantity.toInt()} $unit"
        } else {
            "$quantity $unit"
        }
    }
}

// Enums para unidades (opcional, puedes usar strings)
enum class UnitType(val displayName: String) {
    GRAMS("gramos"),
    KILOGRAMS("kilogramos"),
    LITERS("litros"),
    MILLILITERS("mililitros"),
    UNITS("unidades"),
    PACKAGES("paquetes"),
    CANS("latas"),
    BOTTLES("botellas")
}