// Archivo: com/example/shelfieapp/features/pantry/domain/repository/PantryRepository.kt
package com.example.shelfieapp.features.pantry.domain.repository

import com.example.shelfieapp.features.pantry.domain.model.PantryItem
import kotlinx.coroutines.flow.Flow

interface PantryRepository {
    // Operaciones básicas
    suspend fun addPantryItem(item: PantryItem): Result<PantryItem>
    suspend fun updatePantryItem(item: PantryItem): Result<PantryItem>
    suspend fun deletePantryItem(itemId: String): Result<Unit>
    suspend fun getPantryItem(itemId: String): PantryItem?

    // Consultas
    fun getPantryItems(userId: String): Flow<List<PantryItem>>
    fun getExpiringItems(userId: String, daysThreshold: Int): Flow<List<PantryItem>>
    suspend fun searchPantryItems(userId: String, query: String): List<PantryItem>

    // Estadísticas
    suspend fun getPantryStats(userId: String): PantryStats
    suspend fun getLowStockItems(userId: String, threshold: Double): List<PantryItem>

    // Sincronización
    suspend fun syncUserPantry(userId: String): Result<Unit>

    // Notificaciones
    suspend fun markNotificationSent(itemId: String): Result<Unit>
}

data class PantryStats(
    val totalItems: Int,
    val totalCategories: Int,
    val lowStockItems: Int,
    val totalValue: Double? = null
)