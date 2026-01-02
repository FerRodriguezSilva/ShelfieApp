// Archivo: com/example/shelfieapp/features/pantry/presentation/screens/PantryScreen.kt
package com.example.shelfieapp.features.pantry.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shelfieapp.features.pantry.domain.model.PantryItem
import com.example.shelfieapp.features.pantry.presentation.components.AddPantryItemDialog
import com.example.shelfieapp.features.pantry.presentation.viewmodel.PantryViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantryScreen(
    viewModel: PantryViewModel = koinViewModel(),
    onBack: () -> Unit = {}
) {
    val state by viewModel.state.collectAsState()

    // Diálogo para agregar/editar
    var showDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<PantryItem?>(null) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🧺 Mi Despensa") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Volver")
                    }
                }
            )
        },
        floatingActionButton = {
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
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Mostrar error si existe
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

            // Lista de ingredientes
            if (state.isLoading && state.items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.items.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Tu despensa está vacía",
                            style = MaterialTheme.typography.titleMedium
                        )
                        Text(
                            text = "Presiona el botón + para agregar ingredientes",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(16.dp)
                ) {
                    items(state.items) { item ->
                        PantryItemCard(
                            item = item,
                            onEditClick = {
                                itemToEdit = item
                                showDialog = true
                            },
                            onDeleteClick = { viewModel.deleteItem(item.id) }
                        )
                    }
                }
            }

            // Diálogo para agregar/editar
            if (showDialog) {
                AddPantryItemDialog(
                    item = itemToEdit,
                    onDismiss = {
                        showDialog = false
                        itemToEdit = null
                    },
                    onConfirm = { name, quantity, unit, category ->
                        if (itemToEdit != null) {
                            // Actualizar
                            val updatedItem = itemToEdit!!.copy(
                                name = name,
                                quantity = quantity,
                                unit = unit,
                                category = category
                            )
                            viewModel.updateItem(updatedItem)
                        } else {
                            // Agregar nuevo
                            viewModel.addItem(name, quantity, unit, category)
                        }
                        showDialog = false
                        itemToEdit = null
                    }
                )
            }
        }
    }
}

@Composable
fun PantryItemCard(
    item: PantryItem,
    onEditClick: () -> Unit,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = item.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
                )

                Text(
                    text = "${item.quantity} ${item.unit}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )

                item.category?.let { category ->
                    Text(
                        text = "Categoría: $category",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
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