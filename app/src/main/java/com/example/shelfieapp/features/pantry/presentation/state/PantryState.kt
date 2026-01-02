package com.example.shelfieapp.features.pantry.presentation.state

import com.example.shelfieapp.features.pantry.domain.model.PantryItem

data class PantryState(
    val items: List<PantryItem> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)