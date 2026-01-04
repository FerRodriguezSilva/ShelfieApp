// Archivo: com/example/shelfieapp/ShelfieApp.kt
package com.example.shelfieapp

import android.app.Application
import androidx.work.*
import com.example.shelfieapp.di.appModule
import com.example.shelfieapp.features.pantry.worker.ExpirationCheckWorker
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.startKoin
import java.util.concurrent.TimeUnit

class ShelfieApp : Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidContext(this@ShelfieApp)
            modules(appModule)
        }

        // Configurar WorkManager para notificaciones periódicas
        setupExpirationNotifications()
    }

    private fun setupExpirationNotifications() {
        // Solo programar si no hay un trabajo ya programado
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val checkRequest = PeriodicWorkRequestBuilder<ExpirationCheckWorker>(
            15, TimeUnit.MINUTES  // Para testing: cada 15 minutos
            // En producción: 1, TimeUnit.DAYS
        )
            .setConstraints(constraints)
            .setInitialDelay(1, TimeUnit.MINUTES)  // Esperar 1 minuto
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "expiration_check",
            ExistingPeriodicWorkPolicy.KEEP,  // Si ya existe, mantenerlo
            checkRequest
        )
    }
}