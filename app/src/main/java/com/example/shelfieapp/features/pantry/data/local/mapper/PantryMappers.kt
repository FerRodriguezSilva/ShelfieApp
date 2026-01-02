package com.example.shelfieapp.features.pantry.data.local.mapper

import com.example.shelfieapp.features.pantry.data.local.entity.PantryItemEntity
import com.example.shelfieapp.features.pantry.domain.model.PantryItem

// Convertir de Entity a Domain
fun PantryItemEntity.toDomainModel(): PantryItem {
    return PantryItem(
        id = this.id,
        userId = this.userId,
        name = this.name,
        quantity = this.quantity,
        unit = this.unit,
        category = this.category,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        isActive = this.isActive
    )
}

// Convertir de Domain a Entity
fun PantryItem.toEntity(isSynced: Boolean = false): PantryItemEntity {
    return PantryItemEntity(
        id = this.id,
        userId = this.userId,
        name = this.name,
        quantity = this.quantity,
        unit = this.unit,
        category = this.category,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt,
        isActive = this.isActive,
        isSynced = isSynced
    )
}