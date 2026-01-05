// Archivo: com/example/shelfieapp/features/home/presentation/HomeScreen.kt
package com.example.shelfieapp.features.home.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import com.example.shelfieapp.features.home.presentation.components.AppBar
import com.example.shelfieapp.features.home.presentation.components.DrawerMenu
import com.example.shelfieapp.features.home.presentation.components.PantrySummary
import com.example.shelfieapp.features.home.presentation.components.QuickActions
import com.example.shelfieapp.features.home.presentation.components.RecipeSuggestions
import com.example.shelfieapp.features.home.presentation.viewmodel.HomeViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun HomeScreen(
    viewModel: HomeViewModel = koinViewModel(),
    onNavigate: (String) -> Unit
) {
    val state = viewModel.state.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Recargar datos cuando la pantalla se vuelve a mostrar
    LaunchedEffect(Unit) {
        viewModel.refresh()
    }

    DrawerMenu(
        drawerState = drawerState,
        coroutineScope = coroutineScope,
        onItemClick = { route ->
            onNavigate(route)
        }
    ) {
        Scaffold(
            topBar = {
                AppBar(
                    onMenuClick = {
                        coroutineScope.launch {
                            drawerState.open()
                        }
                    },
                    onSearchClick = { /* TODO */ }
                )
            }
        ) { paddingValues ->
            if (state.value.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            } else if (state.value.error != null) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "Error: ${state.value.error}",
                            color = MaterialTheme.colorScheme.error
                        )
                        Button(
                            onClick = { viewModel.refresh() },
                            modifier = Modifier.padding(top = 16.dp)
                        ) {
                            Text("Reintentar")
                        }
                    }
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                ) {
                    // Saludo personalizado
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "👋 Hola, ${state.value.username}!",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Text(
                            text = "¿Qué cocinamos hoy? 🍳",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    // Resumen de despensa
                    PantrySummary(
                        totalIngredients = state.value.totalIngredients,
                        lowStockItems = state.value.lowStockItems,
                        onViewPantry = { onNavigate("pantry") }
                    )

                    // Recetas que puedes hacer
                    if (state.value.canMakeRecipes.isNotEmpty()) {
                        RecipeSuggestions(
                            title = "🍲 Recetas que puedes hacer ahora",
                            recipes = state.value.canMakeRecipes,
                            onViewAll = { onNavigate("recipes") }
                        )
                    }

                    // Acciones rápidas
                    QuickActions(
                        onAddIngredient = {
                            onNavigate("pantry")
                        }
                    )

                    // Recetas sugeridas
                    if (state.value.suggestedRecipes.isNotEmpty()) {
                        RecipeSuggestions(
                            title = "⭐ Recetas sugeridas",
                            recipes = state.value.suggestedRecipes,
                            onViewAll = { onNavigate("recipes") }
                        )
                    }

                    // Mensaje si no hay recetas
                    if (state.value.canMakeRecipes.isEmpty() && state.value.suggestedRecipes.isEmpty()) {
                        Card(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Text(
                                    text = "🍳 No hay recetas disponibles",
                                    style = MaterialTheme.typography.titleMedium
                                )
                                Text(
                                    text = "Agrega ingredientes a tu despensa para ver recetas sugeridas",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(top = 8.dp)
                                )
                                Button(
                                    onClick = { onNavigate("pantry") },
                                    modifier = Modifier.padding(top = 16.dp)
                                ) {
                                    Text("Agregar ingredientes")
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}