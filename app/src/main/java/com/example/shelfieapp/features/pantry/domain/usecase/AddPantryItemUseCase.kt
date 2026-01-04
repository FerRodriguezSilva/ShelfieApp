// Archivo: com/example/shelfieapp/features/pantry/domain/usecase/AddPantryItemUseCase.kt
package com.example.shelfieapp.features.pantry.domain.usecase

import com.example.shelfieapp.features.pantry.domain.model.PantryItem
import com.example.shelfieapp.features.pantry.domain.repository.PantryRepository

class AddPantryItemUseCase(
    private val repository: PantryRepository
) {
    suspend operator fun invoke(item: PantryItem): Result<PantryItem> {
        // Validaciones
        if (item.name.isBlank()) {
            return Result.failure(Exception("El nombre del ingrediente es requerido"))
        }

        if (item.quantity <= 0) {
            return Result.failure(Exception("La cantidad debe ser mayor a cero"))
        }

        if (item.unit.isBlank()) {
            return Result.failure(Exception("La unidad de medida es requerida"))
        }

        if (item.expirationDate <= System.currentTimeMillis()) {
            return Result.failure(Exception("La fecha de vencimiento debe ser futura"))
        }

        return repository.addPantryItem(item)
    }
}