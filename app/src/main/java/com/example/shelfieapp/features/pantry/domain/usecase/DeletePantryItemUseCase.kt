package com.example.shelfieapp.features.pantry.domain.usecase

import com.example.shelfieapp.features.pantry.domain.repository.PantryRepository

class DeletePantryItemUseCase(
    private val repository: PantryRepository
) {
    suspend operator fun invoke(itemId: String): Result<Unit> {
        return repository.deletePantryItem(itemId)
    }
}