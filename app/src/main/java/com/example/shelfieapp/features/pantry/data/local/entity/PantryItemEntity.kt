// Archivo: com/example/shelfieapp/features/pantry/data/local/entity/PantryItemEntity.kt
package com.example.shelfieapp.features.pantry.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pantry_items")
data class PantryItemEntity(
    @PrimaryKey
    val id: String,
    val userId: String,
    val name: String,
    val quantity: Double,
    val unit: String,
    val expirationDate: Long, // NUEVO
    val createdAt: Long,
    val updatedAt: Long,
    val isActive: Boolean = true,
    val isSynced: Boolean = false,
    val notificationSent: Boolean = false // NUEVO
)