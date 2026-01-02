package com.example.shelfieapp.features.pantry.data.remote

import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.shelfieapp.features.pantry.domain.model.PantryItem
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await

class FirebasePantryDataSource {

    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    private fun getUserPantryRef(userId: String): DatabaseReference {
        return database.getReference("users").child(userId).child("pantry")
    }

    suspend fun addPantryItem(userId: String, item: PantryItem): Result<PantryItem> {
        return try {
            val itemRef = getUserPantryRef(userId).child(item.id)

            // Usar mutableMapOf en lugar de hashMapOf
            val itemData = mutableMapOf<String, Any?>()
            itemData["id"] = item.id
            itemData["name"] = item.name
            itemData["quantity"] = item.quantity
            itemData["unit"] = item.unit
            itemData["category"] = item.category ?: ""
            itemData["createdAt"] = item.createdAt
            itemData["updatedAt"] = item.updatedAt
            itemData["isActive"] = item.isActive

            itemRef.setValue(itemData).await()
            Result.success(item)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun updatePantryItem(userId: String, item: PantryItem): Result<PantryItem> {
        return try {
            // Usar mutableMapOf
            val updates = mutableMapOf<String, Any?>()
            updates["name"] = item.name
            updates["quantity"] = item.quantity
            updates["unit"] = item.unit
            updates["category"] = item.category ?: ""
            updates["updatedAt"] = System.currentTimeMillis()

            getUserPantryRef(userId).child(item.id).updateChildren(updates).await()
            val updatedItem = item.copy(updatedAt = System.currentTimeMillis())
            Result.success(updatedItem)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePantryItem(userId: String, itemId: String): Result<Unit> {
        return try {
            getUserPantryRef(userId).child(itemId).removeValue().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getPantryItem(userId: String, itemId: String): PantryItem? {
        return try {
            val snapshot = getUserPantryRef(userId).child(itemId).get().await()
            if (snapshot.exists()) {
                snapshot.toPantryItem(userId)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun observePantryItems(userId: String): Flow<List<PantryItem>> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<PantryItem>()

                for (child in snapshot.children) {
                    val item = child.toPantryItem(userId)
                    if (item != null && item.isActive) {
                        items.add(item)
                    }
                }

                trySend(items)
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        getUserPantryRef(userId).addValueEventListener(listener)

        awaitClose {
            getUserPantryRef(userId).removeEventListener(listener)
        }
    }

    private fun DataSnapshot.toPantryItem(userId: String): PantryItem? {
        return try {
            PantryItem(
                id = child("id").getValue(String::class.java) ?: this.key ?: "",
                userId = userId,
                name = child("name").getValue(String::class.java) ?: "",
                quantity = child("quantity").getValue(Double::class.java) ?: 0.0,
                unit = child("unit").getValue(String::class.java) ?: "",
                category = child("category").getValue(String::class.java),
                createdAt = child("createdAt").getValue(Long::class.java) ?: 0,
                updatedAt = child("updatedAt").getValue(Long::class.java) ?: 0,
                isActive = child("isActive").getValue(Boolean::class.java) ?: true
            )
        } catch (e: Exception) {
            null
        }
    }
}