// Archivo: com/example/shelfieapp/features/recipes/data/local/mapper/RecipeMapper.kt
package com.example.shelfieapp.features.recipes.data.local.mapper

import com.example.shelfieapp.features.recipes.data.local.entity.RecipeEntity
import com.example.shelfieapp.features.recipes.domain.model.Recipe
import com.example.shelfieapp.features.recipes.domain.model.RecipeIngredient
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

// Convertir de Entity a Domain
fun RecipeEntity.toDomainModel(): Recipe {
    val ingredientsType = object : TypeToken<List<RecipeIngredient>>() {}.type
    val ingredients = Gson().fromJson<List<RecipeIngredient>>(this.ingredientsJson, ingredientsType)

    return Recipe(
        id = this.id,
        name = this.name,
        description = this.description,
        instructions = this.instructions,
        preparationTime = this.preparationTime,
        ingredients = ingredients ?: emptyList(),
        isFavorite = this.isFavorite,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}

// Convertir de Domain a Entity
fun Recipe.toEntity(): RecipeEntity {
    val ingredientsJson = Gson().toJson(this.ingredients)

    return RecipeEntity(
        id = this.id,
        name = this.name,
        description = this.description,
        instructions = this.instructions,
        preparationTime = this.preparationTime,
        ingredientsJson = ingredientsJson,
        isFavorite = this.isFavorite,
        createdAt = this.createdAt,
        updatedAt = this.updatedAt
    )
}