// Archivo: com/example/shelfieapp/features/pantry/domain/service/ExpirationNotificationService.kt
package com.example.shelfieapp.features.pantry.domain.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.util.Log
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
        private const val ONE_DAY_IN_MILLIS = 24 * 60 * 60 * 1000L
    }

    suspend fun checkAndSendNotifications(userId: String) {
        val currentTime = System.currentTimeMillis()
        val oneDayLater = currentTime + ONE_DAY_IN_MILLIS

        // Obtener todos los items
        val allItemsFlow = pantryRepository.getPantryItems(userId)

        withContext(Dispatchers.IO) {
            // Obtener el primer valor del flow
            val items = try {
                runCatching {
                    allItemsFlow.first()
                }.getOrElse { emptyList() }
            } catch (e: Exception) {
                emptyList()
            }

            // 1. Items que vencen mañana (dentro de 24 horas)
            val expiringTomorrowItems = items.filter { item: PantryItem ->
                val oneDayBefore = item.expirationDate - ONE_DAY_IN_MILLIS
                currentTime in oneDayBefore..item.expirationDate && !item.notificationSent
            }

            // 2. Items ya vencidos
            val expiredItems = items.filter { item: PantryItem ->
                item.expirationDate < currentTime && !item.notificationSent
            }

            Log.d("NotificationService", "📊 Items encontrados:")
            Log.d("NotificationService", "  - Vencen mañana: ${expiringTomorrowItems.size}")
            Log.d("NotificationService", "  - Ya vencidos: ${expiredItems.size}")

            // Enviar notificaciones
            if (expiringTomorrowItems.isNotEmpty()) {
                sendExpiringTomorrowNotification(expiringTomorrowItems)
                // Marcar como notificados
                expiringTomorrowItems.forEach { item ->
                    runCatching {
                        pantryRepository.markNotificationSent(item.id)
                        Log.d("NotificationService", "✅ Marcado como notificado: ${item.name}")
                    }.onFailure { e ->
                        Log.e("NotificationService", "❌ Error al marcar notificación: ${e.message}")
                    }
                }
            }

            if (expiredItems.isNotEmpty()) {
                sendExpiredNotification(expiredItems)
                // Marcar como notificados
                expiredItems.forEach { item ->
                    runCatching {
                        pantryRepository.markNotificationSent(item.id)
                        Log.d("NotificationService", "✅ Marcado como notificado: ${item.name}")
                    }.onFailure { e ->
                        Log.e("NotificationService", "❌ Error al marcar notificación: ${e.message}")
                    }
                }
            }

            if (expiringTomorrowItems.isEmpty() && expiredItems.isEmpty()) {
                Log.d("NotificationService", "✅ No hay items que requieran notificación")
            }
        }
    }

    private fun sendExpiringTomorrowNotification(items: List<PantryItem>) {
        createNotificationChannel()

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val now = System.currentTimeMillis()

        // Calcular horas restantes para el item más próximo
        val nearestItem = items.minByOrNull { it.expirationDate }
        val hoursLeft = nearestItem?.let {
            ((it.expirationDate - now) / (60 * 60 * 1000)).toInt()
        } ?: 0

        val itemNames = items.take(3).joinToString(", ") { it.name }
        val remainingCount = items.size - 3

        val title = if (items.size == 1) {
            "⏰ ${items[0].name} vence mañana"
        } else {
            "⏰ ${items.size} ingredientes vencen mañana"
        }

        val content = if (items.size == 1) {
            "${items[0].name} vence en $hoursLeft horas (${dateFormat.format(Date(nearestItem!!.expirationDate))})"
        } else if (remainingCount > 0) {
            "$itemNames y $remainingCount más vencen en las próximas 24 horas"
        } else {
            "$itemNames vencen en las próximas 24 horas"
        }

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .setWhen(System.currentTimeMillis())

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_EXPIRING, notificationBuilder.build())

        Log.d("NotificationService", "📨 Notificación enviada: $title")
    }

    private fun sendExpiredNotification(items: List<PantryItem>) {
        createNotificationChannel()

        val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        val itemNames = items.take(3).joinToString(", ") { it.name }
        val remainingCount = items.size - 3

        val title = if (items.size == 1) {
            "⚠️ ${items[0].name} ha vencido"
        } else {
            "⚠️ ${items.size} ingredientes han vencido"
        }

        val content = if (items.size == 1) {
            "${items[0].name} venció el ${dateFormat.format(Date(items[0].expirationDate))}. ¡Revisa tu despensa!"
        } else if (remainingCount > 0) {
            "$itemNames y $remainingCount más han vencido. ¡Revisa tu despensa!"
        } else {
            "$itemNames han vencido. ¡Revisa tu despensa!"
        }

        val notificationBuilder = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setContentTitle(title)
            .setContentText(content)
            .setStyle(NotificationCompat.BigTextStyle().bigText(content))
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setAutoCancel(true)
            .setWhen(System.currentTimeMillis())

        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.notify(NOTIFICATION_ID_EXPIRED, notificationBuilder.build())

        Log.d("NotificationService", "📨 Notificación enviada: $title")
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // Canal principal
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones sobre ingredientes próximos a vencer o vencidos"
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 250, 250, 250)
                enableLights(true)
                lightColor = android.graphics.Color.RED
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // Eliminar canal existente si hay (para actualizaciones)
            notificationManager.deleteNotificationChannel(CHANNEL_ID)
            notificationManager.createNotificationChannel(channel)

            Log.d("NotificationService", "📱 Canal de notificaciones creado")
        }
    }
}