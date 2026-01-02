package com.example.shelfieapp.features.pantry.data.local.dao

import androidx.room.*
import com.example.shelfieapp.features.pantry.data.local.entity.PantryItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PantryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItem(item: PantryItemEntity)

    @Update
    suspend fun updateItem(item: PantryItemEntity)

    @Delete
    suspend fun deleteItem(item: PantryItemEntity)

    @Query("DELETE FROM pantry_items WHERE id = :itemId")
    suspend fun deleteItemById(itemId: String)

    @Query("SELECT * FROM pantry_items WHERE userId = :userId AND isActive = 1 ORDER BY name ASC")
    fun getItemsByUser(userId: String): Flow<List<PantryItemEntity>>

    @Query("SELECT * FROM pantry_items WHERE userId = :userId AND category = :category AND isActive = 1 ORDER BY name ASC")
    fun getItemsByCategory(userId: String, category: String): Flow<List<PantryItemEntity>>

    @Query("SELECT * FROM pantry_items WHERE userId = :userId AND name LIKE '%' || :query || '%' AND isActive = 1")
    suspend fun searchItems(userId: String, query: String): List<PantryItemEntity>

    @Query("SELECT * FROM pantry_items WHERE userId = :userId AND quantity <= :threshold AND isActive = 1")
    suspend fun getLowStockItems(userId: String, threshold: Double): List<PantryItemEntity>

    @Query("SELECT COUNT(*) FROM pantry_items WHERE userId = :userId AND isActive = 1")
    suspend fun getItemCount(userId: String): Int

    @Query("SELECT COUNT(DISTINCT category) FROM pantry_items WHERE userId = :userId AND isActive = 1 AND category IS NOT NULL")
    suspend fun getCategoryCount(userId: String): Int

    @Query("SELECT * FROM pantry_items WHERE userId = :userId AND isSynced = 0")
    suspend fun getUnsyncedItems(userId: String): List<PantryItemEntity>

    @Query("UPDATE pantry_items SET isSynced = 1 WHERE id = :itemId")
    suspend fun markAsSynced(itemId: String)

    @Query("SELECT * FROM pantry_items WHERE id = :itemId")
    suspend fun getItemById(itemId: String): PantryItemEntity?
}