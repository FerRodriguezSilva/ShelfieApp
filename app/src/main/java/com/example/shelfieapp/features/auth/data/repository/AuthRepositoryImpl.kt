package com.example.shelfieapp.features.auth.data.repository

import com.example.shelfieapp.features.auth.domain.model.LoginRequest
import com.example.shelfieapp.features.auth.domain.model.User
import com.example.shelfieapp.features.auth.domain.repository.AuthRepository
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseDatabase: FirebaseDatabase
) : AuthRepository {

    override suspend fun login(loginRequest: LoginRequest): Result<User> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(
                loginRequest.email,
                loginRequest.password
            ).await()

            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Usuario no encontrado"))

            val userRef = firebaseDatabase.getReference("users/${firebaseUser.uid}")
            val snapshot = userRef.get().await()

            if (snapshot.exists()) {
                val user = User(
                    id = firebaseUser.uid,
                    email = snapshot.child("email").value as? String ?: "",
                    nombre = snapshot.child("nombre").value as? String ?: "",
                    fechaRegistro = snapshot.child("fechaRegistro").value as? String ?: "",
                    fotoPerfil = snapshot.child("fotoPerfil").value as? String ?: ""
                )
                Result.success(user)
            } else {
                Result.failure(Exception("Datos de usuario no encontrados"))
            }

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun register(email: String, password: String, nombre: String): Result<User> {
        return try {
            val authResult = firebaseAuth.createUserWithEmailAndPassword(
                email,
                password
            ).await()

            val firebaseUser = authResult.user
                ?: return Result.failure(Exception("Error al crear usuario"))

            val fechaRegistro = System.currentTimeMillis().toString()

            val user = User(
                id = firebaseUser.uid,
                email = email,
                nombre = nombre,
                fechaRegistro = fechaRegistro,
                fotoPerfil = ""
            )

            val userRef = firebaseDatabase.getReference("users/${firebaseUser.uid}")
            userRef.setValue(
                mapOf(
                    "nombre" to user.nombre,
                    "email" to user.email,
                    "fechaRegistro" to user.fechaRegistro,
                    "fotoPerfil" to user.fotoPerfil
                )
            ).await()

            Result.success(user)

        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    suspend fun logout() {
        firebaseAuth.signOut()
    }
}