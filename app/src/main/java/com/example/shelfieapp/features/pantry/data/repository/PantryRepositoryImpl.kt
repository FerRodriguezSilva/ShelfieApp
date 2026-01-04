// Archivo: com/example/shelfieapp/features/pantry/data/repository/PantryRepositoryImpl.kt
package com.example.shelfieapp.features.pantry.data.repository

import com.example.shelfieapp.features.auth.domain.repository.AuthRepository
import com.example.shelfieapp.features.pantry.data.local.dao.PantryDao
import com.example.shelfieapp.features.pantry.data.local.mapper.toDomainModel
import com.example.shelfieapp.features.pantry.data.local.mapper.toEntity
import com.example.shelfieapp.features.pantry.data.remote.FirebasePantryDataSource
import com.example.shelfieapp.features.pantry.domain.model.PantryItem
import com.example.shelfieapp.features.pantry.domain.repository.PantryRepository
import com.example.shelfieapp.features.pantry.domain.repository.PantryStats
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PantryRepositoryImpl(
    private val pantryDao: PantryDao,
    private val firebaseDataSource: FirebasePantryDataSource,
    private val authRepository: AuthRepository
) : PantryRepository {

    private suspend fun getCurrentUserId(): String? {
        return authRepository.getLoggedInUser()?.id
    }

    override suspend fun addPantryItem(item: PantryItem): Result<PantryItem> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("Usuario no autenticado"))

            // Guardar en Room
            pantryDao.insertItem(item.toEntity(isSynced = false))

            // Intentar sincronizar con Firebase
            firebaseDataSource.addPantryItem(userId, item).onSuccess {
                pantryDao.markAsSynced(item.id)
            }

            Result.success(item)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun updatePantryItem(item: PantryItem): Result<PantryItem> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("Usuario no autenticado"))

            // Actualizar en Room
            val updatedItem = item.copy(updatedAt = System.currentTimeMillis())
            pantryDao.updateItem(updatedItem.toEntity(isSynced = false))

            // Sincronizar con Firebase
            firebaseDataSource.updatePantryItem(userId, updatedItem)
            pantryDao.markAsSynced(item.id)

            Result.success(updatedItem)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun deletePantryItem(itemId: String): Result<Unit> {
        return try {
            val userId = getCurrentUserId() ?: return Result.failure(Exception("Usuario no autenticado"))

            // Eliminar de Room (soft delete)
            val item = pantryDao.getItemById(itemId)
            if (item != null) {
                pantryDao.updateItem(item.copy(isActive = false, isSynced = false))

                // Eliminar de Firebase
                firebaseDataSource.deletePantryItem(userId, itemId)
            }

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPantryItem(itemId: String): PantryItem? {
        return try {
            pantryDao.getItemById(itemId)?.toDomainModel()
        } catch (e: Exception) {
            null
        }
    }

    override fun getPantryItems(userId: String): Flow<List<PantryItem>> {
        return pantryDao.getItemsByUser(userId)
            .map { entities -> entities.map { it.toDomainModel() } }
    }

    override fun getExpiringItems(userId: String, daysThreshold: Int): Flow<List<PantryItem>> {
        return pantryDao.getItemsByUser(userId)
            .map { entities ->
                entities.map { it.toDomainModel() }
                    .filter { item ->
                        val currentTime = System.currentTimeMillis()
                        val thresholdTime = currentTime + (daysThreshold * 24 * 60 * 60 * 1000L)
                        item.expirationDate in (currentTime + 1)..thresholdTime
                    }
            }
    }

    override suspend fun searchPantryItems(userId: String, query: String): List<PantryItem> {
        return try {
            pantryDao.getItemsByUser(userId).first()
                .map { it.toDomainModel() }
                .filter { it.name.contains(query, ignoreCase = true) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    override suspend fun getPantryStats(userId: String): PantryStats {
        val totalItems = pantryDao.getItemCount(userId)

        // Calcular items vencidos y próximos a vencer
        val items = try {
            pantryDao.getItemsByUser(userId).first()
                .map { it.toDomainModel() }
        } catch (e: Exception) {
            emptyList()
        }

        val currentTime = System.currentTimeMillis()
        val weekLater = currentTime + (7 * 24 * 60 * 60 * 1000L)

        val expiredItems = items.count { it.expirationDate < currentTime }
        val expiringItems = items.count { it.expirationDate in (currentTime + 1)..weekLater }

        return PantryStats(
            totalItems = totalItems,
            totalCategories = 0, // Ya no usamos categorías
            lowStockItems = expiredItems + expiringItems
        )
    }

    override suspend fun getLowStockItems(userId: String, threshold: Double): List<PantryItem> {
        return pantryDao.getLowStockItems(userId, threshold).map { it.toDomainModel() }
    }

    override suspend fun syncUserPantry(userId: String): Result<Unit> {
        return try {
            // Sincronizar items no sincronizados
            val unsyncedItems = pantryDao.getUnsyncedItems(userId)

            for (entity in unsyncedItems) {
                val item = entity.toDomainModel()
                firebaseDataSource.addPantryItem(userId, item).onSuccess {
                    pantryDao.markAsSynced(item.id)
                }
            }

            Result.success(Unit)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun markNotificationSent(itemId: String): Result<Unit> {
        return try {
            pantryDao.markNotificationSent(itemId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
    override suspend fun resetNotifications(userId: String): Result<Unit> {
        return try {
            pantryDao.resetNotifications(userId)
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}