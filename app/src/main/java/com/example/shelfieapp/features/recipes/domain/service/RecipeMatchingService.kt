// Archivo: com/example/shelfieapp/features/recipes/domain/service/RecipeMatchingService.kt
package com.example.shelfieapp.features.recipes.domain.service

import com.example.shelfieapp.features.pantry.domain.model.PantryItem
import com.example.shelfieapp.features.recipes.domain.model.Recipe
import com.example.shelfieapp.features.recipes.domain.model.RecipeIngredient

class RecipeMatchingService {

    fun getAvailableRecipes(
        allRecipes: List<Recipe>,
        pantryItems: List<PantryItem>
    ): List<Recipe> {
        return allRecipes.filter { recipe ->
            canMakeRecipe(recipe, pantryItems)
        }
    }

    fun getPossibleRecipes(
        allRecipes: List<Recipe>,
        pantryItems: List<PantryItem>
    ): List<Recipe> {
        return allRecipes.filter { recipe ->
            canPartiallyMakeRecipe(recipe, pantryItems)
        }
    }

    private fun canMakeRecipe(recipe: Recipe, pantryItems: List<PantryItem>): Boolean {
        val pantryMap = pantryItems.associateBy { it.name.lowercase() }

        return recipe.ingredients.all { ingredient ->
            val pantryItem = pantryMap[ingredient.name.lowercase()]
            pantryItem != null && pantryItem.quantity >= ingredient.quantity && !ingredient.isOptional
        }
    }

    private fun canPartiallyMakeRecipe(recipe: Recipe, pantryItems: List<PantryItem>): Boolean {
        val pantryMap = pantryItems.associateBy { it.name.lowercase() }
        val pantryIngredientNames = pantryItems.map { it.name.lowercase() }

        // Al menos un ingrediente no opcional está en la despensa
        val hasAtLeastOneIngredient = recipe.ingredients.any { ingredient ->
            !ingredient.isOptional && pantryMap.containsKey(ingredient.name.lowercase())
        }

        return hasAtLeastOneIngredient
    }

    fun getMissingIngredients(recipe: Recipe, pantryItems: List<PantryItem>): List<RecipeIngredient> {
        val pantryMap = pantryItems.associateBy { it.name.lowercase() }

        return recipe.ingredients.filter { ingredient ->
            val pantryItem = pantryMap[ingredient.name.lowercase()]
            pantryItem == null || pantryItem.quantity < ingredient.quantity
        }
    }

    fun getMatchingPercentage(recipe: Recipe, pantryItems: List<PantryItem>): Int {
        if (recipe.ingredients.isEmpty()) return 0

        val pantryMap = pantryItems.associateBy { it.name.lowercase() }
        val requiredIngredients = recipe.ingredients.filter { !it.isOptional }

        if (requiredIngredients.isEmpty()) return 0

        val matchedIngredients = requiredIngredients.count { ingredient ->
            val pantryItem = pantryMap[ingredient.name.lowercase()]
            pantryItem != null && pantryItem.quantity >= ingredient.quantity
        }

        return (matchedIngredients * 100) / requiredIngredients.size
    }
}