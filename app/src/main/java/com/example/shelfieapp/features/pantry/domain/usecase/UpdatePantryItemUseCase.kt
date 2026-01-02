package com.example.shelfieapp.features.pantry.domain.usecase

import com.example.shelfieapp.features.pantry.domain.model.PantryItem
import com.example.shelfieapp.features.pantry.domain.repository.PantryRepository

class UpdatePantryItemUseCase(
    private val repository: PantryRepository
) {
    suspend operator fun invoke(item: PantryItem): Result<PantryItem> {
        if (item.name.isBlank()) {
            return Result.failure(Exception("El nombre del ingrediente es requerido"))
        }

        if (item.quantity <= 0) {
            return Result.failure(Exception("La cantidad debe ser mayor a cero"))
        }

        return repository.updatePantryItem(item)
    }
}