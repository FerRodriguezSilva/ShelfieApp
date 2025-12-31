// package com.example.shelfieapp.features.auth.data.repository

package com.example.shelfieapp.features.auth.data.repository

import com.example.shelfieapp.features.auth.data.local.dao.UserDao
import com.example.shelfieapp.features.auth.data.local.toDomainModel
import com.example.shelfieapp.features.auth.data.local.toEntity
import com.example.shelfieapp.features.auth.data.remote.FirebaseRealtimeDataSource
import com.example.shelfieapp.features.auth.domain.model.LoginRequest
import com.example.shelfieapp.features.auth.domain.model.RegisterRequest
import com.example.shelfieapp.features.auth.domain.model.User
import com.example.shelfieapp.features.auth.domain.repository.AuthRepository
import java.security.MessageDigest

class AuthRepositoryImpl(
    private val userDao: UserDao,
    private val firebaseDataSource: FirebaseRealtimeDataSource
) : AuthRepository {

    override suspend fun register(registerRequest: RegisterRequest): Result<User> {
        return try {
            println("🔵 [Repository] Registrando: ${registerRequest.email}")

            // 1. Validaciones locales
            if (registerRequest.name.isBlank()) {
                return Result.failure(Exception("El nombre es requerido"))
            }

            if (registerRequest.email.isBlank()) {
                return Result.failure(Exception("El email es requerido"))
            }

            if (registerRequest.password.length < 6) {
                return Result.failure(Exception("La contraseña debe tener al menos 6 caracteres"))
            }

            // 2. Verificar si ya existe localmente
            if (userDao.userExists(registerRequest.email)) {
                return Result.failure(Exception("El email ya está registrado localmente"))
            }

            // 3. Hash de la contraseña
            val passwordHash = hashPassword(registerRequest.password)

            // 4. Registrar en Firebase Realtime DB
            val firebaseResult = firebaseDataSource.registerUser(
                email = registerRequest.email,
                passwordHash = passwordHash,
                name = registerRequest.name
            )

            if (firebaseResult.isFailure) {
                return Result.failure(firebaseResult.exceptionOrNull()!!)
            }

            val firebaseUser = firebaseResult.getOrThrow()
            println("✅ [Firebase] Registro exitoso en Realtime DB")

            // 5. Guardar también en Room (con el hash)
            userDao.insertUser(firebaseUser.toEntity(passwordHash, isLoggedIn = true))

            // 6. Marcar como logueado
            userDao.logoutAllUsers()
            userDao.setUserLoggedIn(firebaseUser.id)

            println("✅ [Room] Usuario guardado localmente")

            Result.success(firebaseUser)

        } catch (e: Exception) {
            println("❌ [Repository] Error en registro: ${e.message}")
            Result.failure(e)
        }
    }

    override suspend fun login(loginRequest: LoginRequest): Result<User> {
        return try {
            println("🔵 [Repository] Login: ${loginRequest.email}")

            // 1. Validaciones básicas
            if (loginRequest.email.isBlank() || loginRequest.password.isBlank()) {
                return Result.failure(Exception("Email y contraseña son requeridos"))
            }

            // 2. Hash de la contraseña
            val passwordHash = hashPassword(loginRequest.password)

            // 3. Intentar login con Firebase (en línea)
            val firebaseResult = firebaseDataSource.loginUser(
                email = loginRequest.email,
                passwordHash = passwordHash
            )

            if (firebaseResult.isSuccess) {
                // 4. Login exitoso en Firebase
                val user = firebaseResult.getOrThrow()

                // 5. Sincronizar con Room
                val existingUser = userDao.getUserByEmail(loginRequest.email)
                if (existingUser == null) {
                    // Guardar nuevo en Room
                    userDao.insertUser(user.toEntity(passwordHash, isLoggedIn = true))
                } else {
                    // Actualizar estado de login
                    userDao.logoutAllUsers()
                    userDao.setUserLoggedIn(user.id)
                }

                println("✅ [Repository] Login exitoso (online)")
                return Result.success(user)
            }

            // 6. Si falla Firebase, intentar con Room (offline)
            println("🌐 [Repository] Intentando login offline...")
            return tryOfflineLogin(loginRequest.email, passwordHash)

        } catch (e: Exception) {
            println("❌ [Repository] Error en login: ${e.message}")
            Result.failure(e)
        }
    }

    private suspend fun tryOfflineLogin(email: String, passwordHash: String): Result<User> {
        return try {
            // 1. Buscar en Room
            val userEntity = userDao.getUserByEmail(email)
                ?: return Result.failure(Exception("Usuario no encontrado"))

            // 2. Verificar password hash
            if (userEntity.passwordHash != passwordHash) {
                return Result.failure(Exception("Contraseña incorrecta"))
            }

            // 3. Marcar como logueado
            userDao.logoutAllUsers()
            userDao.setUserLoggedIn(userEntity.id)

            val user = userEntity.toDomainModel()
            println("✅ [Repository] Login offline exitoso")

            Result.success(user)

        } catch (e: Exception) {
            Result.failure(Exception("Error en login offline: ${e.message}"))
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
            println("✅ [Repository] Logout completado")
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Función para hashear contraseñas
    private fun hashPassword(password: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(password.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}