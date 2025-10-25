package com.example.shelfieapp.features.home.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.launch
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDrawerState
import com.example.shelfieapp.features.home.presentation.components.AppBar
import com.example.shelfieapp.features.home.presentation.components.DrawerMenu
import com.example.shelfieapp.features.home.presentation.components.PantrySummary
import com.example.shelfieapp.features.home.presentation.components.QuickActions
import com.example.shelfieapp.features.home.presentation.components.RecipeSuggestions

@Composable
fun HomeScreen(
    onNavigate: (String) -> Unit
) {
    val drawerState = rememberDrawerState(initialValue = androidx.compose.material3.DrawerValue.Closed)
    val coroutineScope = rememberCoroutineScope()

    // Datos de ejemplo
    val suggestedRecipes = listOf(
        "Omelette de verduras",
        "Pasta con salsa de tomate",
        "Ensalada de atún"
    )

    val featuredRecipes = listOf(
        "Tacos de pollo",
        "Sopa de lentejas",
        "Arroz frito con verduras"
    )

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
                        text = "👋 Hola, Fernando!",
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
                    totalIngredients = 12,
                    lowStockItems = 3,
                    onViewPantry = { onNavigate("pantry") }
                )

                // Recetas sugeridas
                RecipeSuggestions(
                    title = "🍲 Recetas que puedes hacer ahora",
                    recipes = suggestedRecipes,
                    onViewAll = { onNavigate("recipes") }
                )

                // Acciones rápidas
                QuickActions(
                    onAddIngredient = { /* TODO */ }
                )

                // Recomendaciones destacadas
                RecipeSuggestions(
                    title = "⭐ Recomendaciones personalizadas",
                    recipes = featuredRecipes,
                    onViewAll = { onNavigate("recipes") }
                )
            }
        }
    }
}