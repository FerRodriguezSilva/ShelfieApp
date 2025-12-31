// package com.example.shelfieapp.features.auth.data.remote

package com.example.shelfieapp.features.auth.data.remote

import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.shelfieapp.features.auth.domain.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID

class FirebaseRealtimeDataSource {

    // Inicialización SIN KTX (usando getInstance())
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()
    private val usersRef: DatabaseReference = database.getReference("users")

    suspend fun registerUser(
        email: String,
        passwordHash: String,
        name: String
    ): Result<User> {
        return try {
            println("🔵 [Firebase] Registrando usuario: $email")

            // 1. Verificar si el email ya existe
            val emailQuery = usersRef.orderByChild("email").equalTo(email)
            val snapshot = emailQuery.get().await()

            if (snapshot.exists()) {
                // Iterar sobre los hijos para encontrar el email
                for (child in snapshot.children) {
                    val userEmail = child.child("email").getValue(String::class.java)
                    if (userEmail == email) {
                        return Result.failure(Exception("El email ya está registrado"))
                    }
                }
            }

            // 2. Crear nuevo usuario
            val userId = UUID.randomUUID().toString()
            val user = User(
                id = userId,
                email = email,
                name = name,
                password = "" // No enviamos password
            )

            // 3. Guardar en Firebase (SIN KTX)
            val userData = mapOf<String, Any>(
                "id" to userId,
                "email" to email,
                "name" to name,
                "passwordHash" to passwordHash,
                "createdAt" to System.currentTimeMillis(),
                "updatedAt" to System.currentTimeMillis(),
                "isActive" to true
            )

            usersRef.child(userId).setValue(userData).await()
            println("✅ [Firebase] Usuario registrado: $userId")

            Result.success(user)

        } catch (e: Exception) {
            println("❌ [Firebase] Error en registro: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun loginUser(
        email: String,
        passwordHash: String
    ): Result<User> {
        return try {
            println("🔵 [Firebase] Login: $email")

            // 1. Buscar usuario por email
            val query = usersRef.orderByChild("email").equalTo(email)
            val snapshot = query.get().await()

            if (!snapshot.exists()) {
                return Result.failure(Exception("Usuario no encontrado"))
            }

            var userFound: User? = null
            var hashFound: String? = null
            var userIdFound: String? = null

            // 2. Iterar sobre resultados (SIN .ktx.getValue())
            for (child in snapshot.children) {
                val userEmail = child.child("email").getValue(String::class.java)
                if (userEmail == email) {
                    hashFound = child.child("passwordHash").getValue(String::class.java)
                    userIdFound = child.child("id").getValue(String::class.java)
                    val userName = child.child("name").getValue(String::class.java)

                    if (userIdFound != null && userName != null) {
                        userFound = User(
                            id = userIdFound,
                            email = email,
                            name = userName,
                            password = ""
                        )
                    }
                    break
                }
            }

            if (userFound == null) {
                return Result.failure(Exception("Usuario no encontrado"))
            }

            if (hashFound == null) {
                return Result.failure(Exception("Datos de usuario incompletos"))
            }

            // 3. Verificar password hash
            if (hashFound != passwordHash) {
                return Result.failure(Exception("Contraseña incorrecta"))
            }

            println("✅ [Firebase] Login exitoso: ${userFound.id}")
            Result.success(userFound)

        } catch (e: Exception) {
            println("❌ [Firebase] Error en login: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun checkEmailExists(email: String): Boolean {
        return try {
            val query = usersRef.orderByChild("email").equalTo(email)
            val snapshot = query.get().await()

            if (snapshot.exists()) {
                for (child in snapshot.children) {
                    val userEmail = child.child("email").getValue(String::class.java)
                    if (userEmail == email) {
                        return true
                    }
                }
            }
            false
        } catch (e: Exception) {
            false
        }
    }

    suspend fun getUserById(userId: String): User? {
        return try {
            val snapshot = usersRef.child(userId).get().await()

            if (snapshot.exists()) {
                val email = snapshot.child("email").getValue(String::class.java) ?: ""
                val name = snapshot.child("name").getValue(String::class.java) ?: ""

                User(
                    id = userId,
                    email = email,
                    name = name,
                    password = ""
                )
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun observeUser(userId: String): Flow<User?> = callbackFlow {
        val listener = object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val email = snapshot.child("email").getValue(String::class.java) ?: ""
                    val name = snapshot.child("name").getValue(String::class.java) ?: ""

                    val user = User(
                        id = userId,
                        email = email,
                        name = name,
                        password = ""
                    )
                    trySend(user)
                } else {
                    trySend(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                close(error.toException())
            }
        }

        usersRef.child(userId).addValueEventListener(listener)

        awaitClose {
            usersRef.child(userId).removeEventListener(listener)
        }
    }

    suspend fun updateUser(userId: String, updates: Map<String, Any>): Result<Unit> {
        return try {
            usersRef.child(userId).updateChildren(updates).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}