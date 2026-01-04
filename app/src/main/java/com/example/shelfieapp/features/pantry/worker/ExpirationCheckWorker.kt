// Archivo: com/example/shelfieapp/features/pantry/worker/ExpirationCheckWorker.kt
package com.example.shelfieapp.features.pantry.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.example.shelfieapp.features.auth.domain.repository.AuthRepository
import com.example.shelfieapp.features.pantry.domain.service.ExpirationNotificationService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.java.KoinJavaComponent.inject
import java.text.SimpleDateFormat
import java.util.*

class ExpirationCheckWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    private val authRepository: AuthRepository by inject(AuthRepository::class.java)
    private val expirationNotificationService: ExpirationNotificationService by inject(ExpirationNotificationService::class.java)

    override suspend fun doWork(): Result {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault())
        val startTime = System.currentTimeMillis()

        return try {
            Log.d("ExpirationCheckWorker", "👷 Worker iniciado - ${dateFormat.format(Date(startTime))}")

            // Obtener usuario actual
            val currentUser = withContext(Dispatchers.IO) {
                authRepository.getLoggedInUser()
            }

            if (currentUser != null) {
                Log.d("ExpirationCheckWorker", "👤 Usuario encontrado: ${currentUser.email}")

                // Usar el servicio de notificaciones
                expirationNotificationService.checkAndSendNotifications(currentUser.id)

                val endTime = System.currentTimeMillis()
                val duration = (endTime - startTime) / 1000.0

                Log.d("ExpirationCheckWorker", "✅ Worker completado en ${duration}s")
                Log.d("ExpirationCheckWorker", "⏰ Próxima ejecución: cada 12 horas")
            } else {
                Log.d("ExpirationCheckWorker", "⚠️ No hay usuario autenticado, omitiendo verificación")
            }

            Result.success()
        } catch (e: Exception) {
            Log.e("ExpirationCheckWorker", "❌ Error: ${e.message}", e)
            Result.failure()
        }
    }
}