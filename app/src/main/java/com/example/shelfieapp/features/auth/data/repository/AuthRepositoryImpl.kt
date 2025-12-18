package com.example.shelfieapp.features.auth.data.repository

import com.example.shelfieapp.features.auth.data.local.dao.UserDao
import com.example.shelfieapp.features.auth.data.local.toDomainModel
import com.example.shelfieapp.features.auth.data.local.toEntity
import com.example.shelfieapp.features.auth.domain.model.LoginRequest
import com.example.shelfieapp.features.auth.domain.model.RegisterRequest
import com.example.shelfieapp.features.auth.domain.model.User
import com.example.shelfieapp.features.auth.domain.repository.AuthRepository
import java.security.MessageDigest
import java.util.UUID

class AuthRepositoryImpl(
    private val userDao: UserDao
) : AuthRepository {

    override suspend fun register(registerRequest: RegisterRequest): Result<User> {
        return try {
            // Verificar si el usuario ya existe
            if (userDao.userExists(registerRequest.email)) {
                return Result.failure(Exception("El email ya está registrado"))
            }

            // Crear nuevo usuario
            val userId = UUID.randomUUID().toString()
            val passwordHash = hashPassword(registerRequest.password)

            val user = User(
                id = userId,
                email = registerRequest.email,
                name = registerRequest.name,
                password = ""
            )

            // Guardar en Room
            userDao.insertUser(user.toEntity(passwordHash, isLoggedIn = true))

            // Asegurar que solo este usuario esté logueado
            userDao.logoutAllUsers()
            userDao.setUserLoggedIn(userId)

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun login(loginRequest: LoginRequest): Result<User> {
        return try {
            // Buscar usuario por email
            val userEntity = userDao.getUserByEmail(loginRequest.email)
                ?: return Result.failure(Exception("Usuario no encontrado"))

            // Verificar password
            val passwordHash = hashPassword(loginRequest.password)
            if (userEntity.passwordHash != passwordHash) {
                return Result.failure(Exception("Contraseña incorrecta"))
            }

            // Marcar como logueado
            userDao.logoutAllUsers()
            userDao.setUserLoggedIn(userEntity.id)

            Result.success(userEntity.toDomainModel())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun isLoggedIn(): Boolean {
        return try {
            userDao.getLoggedInUser() != null
        } catch (e: Exception) {
            false
        }
    }

    override suspend fun getLoggedInUser(): User? {
        return try {
            userDao.getLoggedInUser()?.toDomainModel()
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun logout(): Result<Unit> {
        return try {
            userDao.logoutAllUsers()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Función simple para hashear password (usa BCrypt en producción)
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}