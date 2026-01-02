package com.example.shelfieapp.features.pantry.domain.usecase

import com.example.shelfieapp.features.pantry.domain.model.PantryItem
import com.example.shelfieapp.features.pantry.domain.repository.PantryRepository
import kotlinx.coroutines.flow.Flow

class GetPantryItemsUseCase(
    private val repository: PantryRepository
) {
    operator fun invoke(userId: String): Flow<List<PantryItem>> {
        return repository.getPantryItems(userId)
    }
}