// Archivo: com/example/shelfieapp/features/pantry/domain/model/PantryItem.kt
package com.example.shelfieapp.features.pantry.domain.model

import java.util.UUID

data class PantryItem(
    val id: String = UUID.randomUUID().toString(),
    val userId: String,
    val name: String,
    val quantity: Double,
    val unit: String,
    val expirationDate: Long, // NUEVO: Fecha de vencimiento en timestamp
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val isActive: Boolean = true,
    val notificationSent: Boolean = false // Para controlar notificaciones enviadas
) {
    fun displayQuantity(): String {
        return if (quantity % 1 == 0.0) {
            "${quantity.toInt()} $unit"
        } else {
            "$quantity $unit"
        }
    }

    // Método para verificar si está próximo a vencer (ej: 7 días antes)
    fun isExpiringSoon(daysThreshold: Int = 7): Boolean {
        val currentTime = System.currentTimeMillis()
        val thresholdMillis = daysThreshold * 24 * 60 * 60 * 1000L
        return expirationDate in (currentTime + 1)..(currentTime + thresholdMillis)
    }

    // Método para verificar si ya venció
    fun isExpired(): Boolean {
        return System.currentTimeMillis() > expirationDate
    }
}