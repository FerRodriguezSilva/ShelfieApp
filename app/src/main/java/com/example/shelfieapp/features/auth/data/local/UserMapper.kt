package com.example.shelfieapp.features.auth.data.local

import com.example.shelfieapp.features.auth.data.local.entity.UserEntity
import com.example.shelfieapp.features.auth.domain.model.User

// Convertir de Entity (Room) a Domain Model
fun UserEntity.toDomainModel(): User {
    return User(
        id = this.id,
        email = this.email,
        name = this.name,
        password = "" // Nunca exponemos el password hash al dominio
    )
}

// Convertir de Domain Model a Entity (Room)
fun User.toEntity(passwordHash: String, isLoggedIn: Boolean = false): UserEntity {
    return UserEntity(
        id = this.id,
        email = this.email,
        name = this.name,
        passwordHash = passwordHash,
        isLoggedIn = isLoggedIn
    )
}