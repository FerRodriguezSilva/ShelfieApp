// Archivo: com/example/shelfieapp/features/pantry/domain/service/ExpirationNotificationService.kt
package com.example.shelfieapp.features.pantry.domain.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.example.shelfieapp.R
import com.example.shelfieapp.features.pantry.domain.model.PantryItem
import com.example.shelfieapp.features.pantry.domain.repository.PantryRepository
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.first
import java.text.SimpleDateFormat
import java.util.*

class ExpirationNotificationService(
    private val context: Context,
    private val pantryRepository: PantryRepository
) {

    companion object {
        const val CHANNEL_ID = "expiration_notifications"
        const val CHANNEL_NAME = "Notificaciones de Vencimiento"
        const val NOTIFICATION_ID_EXPIRING = 1001
        const val NOTIFICATION_ID_EXPIRED = 1002
    }

    suspend fun checkAndSendNotifications(userId: String) {
        val currentTime = System.currentTimeMillis()
        val sevenDaysLater = currentTime + (7 * 24 * 60 * 60 * 1000L)

        // Obtener todos los items
        val allItemsFlow = pantryRepository.getPantryItems(userId)

        withContext(Dispatchers.IO) {
            // Obtener el primer valor del flow
            val items = try {
                // Usar runCatching para manejar excepciones
                runCatching {
                    allItemsFlow.first()
                }.getOrElse { emptyList() }
            } catch (e: Exception) {
                emptyList()
            }

            // Items próximos a vencer (en los próximos 7 días)
            val expiringItems = items.filter { item: PantryItem ->
                item.expirationDate in (currentTime + 1)..sevenDaysLater && !item.notificationSent
            }

            // Items ya vencidos
            val expiredItems = items.filter { item: PantryItem ->
                item.expirationDate < currentTime
            }

            // Enviar notificaciones
            if (expiringItems.isNotEmpty()) {
                sendExpiringNotification(expiringItems)
            }

            if (expiredItems.isNotEmpty()) {
                sendExpiredNotification(expiredItems)
            }
        }
    }

    private fun sendExpiringNotification(items: List<PantryItem>) {
        createNotificationChannel()

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val itemNames = items.take(3).joinToString(", ") { it.name }
        val remainingCount = items.size - 3

        val title = if (items.size == 1) {
            "1 ingrediente próximo a vencer"
        } else {
            "${items.size} ingredientes próximos a vencer"
        }

        val content = if (remainingCount > 0) {
            "$itemNames y $remainingCount más"
        } else {
            itemNames
        }

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Usa un icono que tengas
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_EXPIRING, notificationBuilder.build())
    }

    private fun sendExpiredNotification(items: List<PantryItem>) {
        createNotificationChannel()

        val itemNames = items.take(3).joinToString(", ") { it.name }
        val remainingCount = items.size - 3

        val title = if (items.size == 1) {
            "1 ingrediente vencido"
        } else {
            "${items.size} ingredientes vencidos"
        }

        val content = if (remainingCount > 0) {
            "$itemNames y $remainingCount más"
        } else {
            itemNames
        }

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground) // Usa un icono que tengas
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_EXPIRED, notificationBuilder.build())
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            ).apply {
                description = "Notificaciones sobre ingredientes próximos a vencer"
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }
}