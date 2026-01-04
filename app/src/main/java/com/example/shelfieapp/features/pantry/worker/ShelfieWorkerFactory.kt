// Archivo: com/example/shelfieapp/worker/ShelfieWorkerFactory.kt
package com.example.shelfieapp.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.example.shelfieapp.features.auth.domain.repository.AuthRepository
import com.example.shelfieapp.features.pantry.domain.repository.PantryRepository
import com.example.shelfieapp.features.pantry.worker.ExpirationCheckWorker
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class ShelfieWorkerFactory : WorkerFactory(), KoinComponent {

    private val authRepository: AuthRepository by inject()
    private val pantryRepository: PantryRepository by inject()

    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            ExpirationCheckWorker::class.java.name -> {
                ExpirationCheckWorker(appContext, workerParameters).apply {
                    // Puedes inyectar dependencias aquí si es necesario
                }
            }
            else -> null
        }
    }
}