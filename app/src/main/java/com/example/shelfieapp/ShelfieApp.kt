// Archivo: com/example/shelfieapp/ShelfieApp.kt
package com.example.shelfieapp

import android.app.Application
import android.util.Log
import androidx.work.*
import com.example.shelfieapp.di.appModule
import com.example.shelfieapp.features.pantry.worker.ExpirationCheckWorker
import com.example.shelfieapp.features.recipes.domain.usecase.SyncRecipesUseCase
import com.google.firebase.BuildConfig
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.koin.androidContext
import org.koin.core.context.GlobalContext
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
        syncRecipesOnStartup()
    }

    private fun setupExpirationNotifications() {
        // Solo programar si no hay un trabajo ya programado
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(true) // No ejecutar si batería baja
            .build()

        // Configurar frecuencia según build type
        val checkInterval = if (BuildConfig.DEBUG) {
            1L // hora (testing)
        } else {
            12L // horas (producción)
        }

        val timeUnit = TimeUnit.HOURS

        val checkRequest = PeriodicWorkRequestBuilder<ExpirationCheckWorker>(
            checkInterval, timeUnit
        )
            .setConstraints(constraints)
            .setInitialDelay(10, TimeUnit.SECONDS) // Pequeño delay inicial
            .setBackoffCriteria(
                BackoffPolicy.EXPONENTIAL,
                30,
                TimeUnit.SECONDS
            )
            .build()

        WorkManager.getInstance(this).enqueueUniquePeriodicWork(
            "expiration_check",
            ExistingPeriodicWorkPolicy.KEEP,  // Si ya existe, mantenerlo
            checkRequest
        )

        Log.d("ShelfieApp", "✅ Worker programado para ejecutarse cada $checkInterval ${timeUnit.name.lowercase()}")
    }

    // Método para forzar una verificación inmediata (útil para testing)
    fun forceExpirationCheck() {
        val oneTimeRequest = OneTimeWorkRequestBuilder<ExpirationCheckWorker>()
            .setConstraints(
                Constraints.Builder()
                    .setRequiredNetworkType(NetworkType.CONNECTED)
                    .build()
            )
            .build()

        WorkManager.getInstance(this).enqueue(oneTimeRequest)
        Log.d("ShelfieApp", "🔔 Verificación forzada de notificaciones")
    }
    private fun syncRecipesOnStartup() {
        val scope = CoroutineScope(Dispatchers.IO)
        scope.launch {
            try {
                // Obtener el use case manualmente para sincronizar
                val koin = GlobalContext.get()
                val syncUseCase = koin.get<SyncRecipesUseCase>()
                syncUseCase()
                Log.d("ShelfieApp", "✅ Recetas sincronizadas al iniciar")
            } catch (e: Exception) {
                Log.e("ShelfieApp", "❌ Error sincronizando recetas: ${e.message}")
            }
        }
    }
}