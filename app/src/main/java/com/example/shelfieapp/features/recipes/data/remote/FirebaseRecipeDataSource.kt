// Archivo: com/example/shelfieapp/features/recipes/data/remote/FirebaseRecipeDataSource.kt
package com.example.shelfieapp.features.recipes.data.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.shelfieapp.features.recipes.domain.model.Recipe
import com.example.shelfieapp.features.recipes.domain.model.RecipeIngredient
import com.google.gson.Gson
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebaseRecipeDataSource {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val recipesRef: DatabaseReference = database.getReference("recipes")
    private val gson = Gson()

    suspend fun getAllRecipes(): Result<List<Recipe>> {
        return try {
            val snapshot = recipesRef.get().await()
            val recipes = mutableListOf<Recipe>()

            for (child in snapshot.children) {
                val recipe = child.toRecipe()
                recipe?.let { recipes.add(it) }
            }

            Result.success(recipes)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun observeAllRecipes(): Flow<List<Recipe>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val recipes = mutableListOf<Recipe>()

                for (child in snapshot.children) {
                    val recipe = child.toRecipe()
                    recipe?.let { recipes.add(it) }
                }

                trySend(recipes)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        recipesRef.addValueEventListener(listener)

        awaitClose {
            recipesRef.removeEventListener(listener)
        }
    }

    suspend fun getRecipeById(recipeId: String): Result<Recipe?> {
        return try {
            val snapshot = recipesRef.child(recipeId).get().await()
            val recipe = snapshot.toRecipe()
            Result.success(recipe)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addRecipe(recipe: Recipe): Result<Recipe> {
        return try {
            recipesRef.child(recipe.id).setValue(recipe.toMap()).await()
            Result.success(recipe)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updateRecipe(recipe: Recipe): Result<Recipe> {
        return try {
            recipesRef.child(recipe.id).updateChildren(recipe.toMap()).await()
            Result.success(recipe)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteRecipe(recipeId: String): Result<Unit> {
        return try {
            recipesRef.child(recipeId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    private fun DataSnapshot.toRecipe(): Recipe? {
        return try {
            Recipe(
                id = child("id").getValue(String::class.java) ?: this.key ?: "",
                name = child("name").getValue(String::class.java) ?: "",
                description = child("description").getValue(String::class.java) ?: "",
                instructions = child("instructions").getValue(String::class.java) ?: "",
                preparationTime = child("preparationTime").getValue(Int::class.java) ?: 0,
                ingredients = parseIngredients(child("ingredients")),
                isFavorite = false,  // ← CAMBIO 1: Poner false por defecto
                createdAt = System.currentTimeMillis(),  // ← CAMBIO 2: Usar tiempo actual
                updatedAt = System.currentTimeMillis()   // ← CAMBIO 3: Usar tiempo actual
            )
        } catch (e: Exception) {
            null
        }
    }

    private fun parseIngredients(snapshot: DataSnapshot): List<RecipeIngredient> {
        val ingredients = mutableListOf<RecipeIngredient>()

        for (child in snapshot.children) {
            try {
                val ingredient = RecipeIngredient(
                    name = child.child("name").getValue(String::class.java) ?: "",
                    quantity = child.child("quantity").getValue(Double::class.java) ?: 0.0,
                    unit = child.child("unit").getValue(String::class.java) ?: "",
                    isOptional = child.child("optional").getValue(Boolean::class.java) ?: false  // ← CAMBIO: "optional" no "isOptional"
                )
                ingredients.add(ingredient)
            } catch (e: Exception) {
                // Ignorar ingredientes mal formados
            }
        }

        return ingredients
    }

    private fun Recipe.toMap(): Map<String, Any?> {
        return mapOf(
            "id" to id,
            "name" to name,
            "description" to description,
            "instructions" to instructions,
            "preparationTime" to preparationTime,
            "ingredients" to ingredients.map { ingredient ->
                mapOf(
                    "name" to ingredient.name,
                    "quantity" to ingredient.quantity,
                    "unit" to ingredient.unit,
                    "isOptional" to ingredient.isOptional
                )
            },
            "isFavorite" to isFavorite,
            "createdAt" to createdAt,
            "updatedAt" to updatedAt
        )
    }
}