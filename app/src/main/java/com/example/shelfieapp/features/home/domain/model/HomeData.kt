package com.example.shelfieapp.features.home.domain.model

data class HomeData(
    val userName: String = "User",
    val pantrySummary: PantrySummary = PantrySummary(),
    val suggestedRecipes: List<RecipeSuggestion> = emptyList(),
    val featuredRecipes: List<RecipeSuggestion> = emptyList()
)

data class PantrySummary(
    val totalIngredients: Int = 12,
    val lowStockItems: Int = 3
)

data class RecipeSuggestion(
    val id: String,
    val name: String,
    val description: String
)