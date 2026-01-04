package com.example.shelfieapp.features.pantry.presentation.screens

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.NotificationCompat
import com.example.shelfieapp.features.pantry.domain.model.PantryItem
import com.example.shelfieapp.features.pantry.presentation.components.AddPantryItemDialog
import com.example.shelfieapp.features.pantry.presentation.viewmodel.PantryViewModel
import org.koin.androidx.compose.koinViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryScreen(
    viewModel: PantryViewModel = koinViewModel(),
    onBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current

    var showDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<PantryItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🧺 Mi Despensa") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                }
            )
        },
        floatingActionButton = {
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // 🔔 Botón de prueba de notificación
                FloatingActionButton(
                    onClick = {
                        sendTestNotification(context)
                    },
                    modifier = Modifier.size(48.dp),
                    containerColor = MaterialTheme.colorScheme.tertiary
                ) {
                    Text("🔔")
                }

                // ➕ Botón original
                FloatingActionButton(
                    onClick = {
                        itemToEdit = null
                        showDialog = true
                    },
                    containerColor = MaterialTheme.colorScheme.primary
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Agregar")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {

            state.error?.let { error ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }

            when {
                state.isLoading && state.items.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.items.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Tu despensa está vacía")
                            Text(
                                "Presiona + para agregar ingredientes",
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(state.items) { item ->
                            PantryItemCard(
                                item = item,
                                onEditClick = {
                                    itemToEdit = item
                                    showDialog = true
                                },
                                onDeleteClick = {
                                    viewModel.deleteItem(item.id)
                                }
                            )
                        }
                    }
                }
            }

            if (showDialog) {
                AddPantryItemDialog(
                    item = itemToEdit,
                    onDismiss = {
                        showDialog = false
                        itemToEdit = null
                    },
                    onConfirm = { name, quantity, unit, expirationDate ->
                        if (itemToEdit != null) {
                            viewModel.updateItem(
                                itemToEdit!!.copy(
                                    name = name,
                                    quantity = quantity,
                                    unit = unit,
                                    expirationDate = expirationDate
                                )
                            )
                        } else {
                            viewModel.addItem(name, quantity, unit, expirationDate)
                        }
                        showDialog = false
                        itemToEdit = null
                    }
                )
            }
        }
    }
}

/* =======================
   🔔 NOTIFICACIÓN DE PRUEBA
   ======================= */
private fun sendTestNotification(context: Context) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            "expiration_notifications",
            "Notificaciones de Vencimiento",
            NotificationManager.IMPORTANCE_DEFAULT
        ).apply {
            description = "Notificaciones sobre ingredientes próximos a vencer"
        }

        val manager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.createNotificationChannel(channel)
    }

    val notification = NotificationCompat.Builder(context, "expiration_notifications")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("🔔 Test de Notificación")
        .setContentText("¡Las notificaciones están funcionando correctamente!")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setAutoCancel(true)
        .build()

    val manager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    manager.notify(9999, notification)
}

/* =======================
   🧾 CARD DE INGREDIENTE
   ======================= */
@Composable
fun PantryItemCard(
    item: PantryItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(item.expirationDate))
    val now = System.currentTimeMillis()

    val isExpired = now > item.expirationDate
    val isExpiringSoon = item.expirationDate in (now + 1)..(now + 7 * 24 * 60 * 60 * 1000L)

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = when {
                isExpired -> MaterialTheme.colorScheme.errorContainer
                isExpiringSoon -> MaterialTheme.colorScheme.tertiaryContainer
                else -> MaterialTheme.colorScheme.surface
            }
        )
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(item.name, style = MaterialTheme.typography.titleMedium)
                Text("${item.quantity} ${item.unit}")
                Text("Vence: $formattedDate")

                if (isExpired) Text("¡Vencido!", color = MaterialTheme.colorScheme.error)
                else if (isExpiringSoon) Text("Próximo a vencer")
            }

            Row {
                IconButton(onClick = onEditClick) {
                    Icon(Icons.Default.Edit, contentDescription = "Editar")
                }
                IconButton(onClick = onDeleteClick) {
                    Icon(Icons.Default.Delete, contentDescription = "Eliminar")
                }
            }
        }
    }
}
