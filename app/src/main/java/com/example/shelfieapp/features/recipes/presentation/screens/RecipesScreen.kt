// Archivo: com/example/shelfieapp/features/recipes/presentation/screens/RecipesScreen.kt
package com.example.shelfieapp.features.recipes.presentation.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.shelfieapp.features.recipes.presentation.components.RecipeCard
import com.example.shelfieapp.features.recipes.presentation.viewmodel.RecipesViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipesScreen(
    viewModel: RecipesViewModel = koinViewModel(),
    onBack: () -> Unit = {},
    onRecipeClick: (String) -> Unit = {}
) {
    val state by viewModel.state.collectAsState()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("🍳 Recetas") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Volver"
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: Implementar búsqueda */ }) {
                        Icon(
                            Icons.Filled.Search,
                            contentDescription = "Buscar recetas"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Pestañas
            TabRow(selectedTabIndex = selectedTab) {
                Tab(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    text = { Text("Todas") }
                )
                Tab(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    text = { Text("Puedo hacer") }
                )
                Tab(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    text = { Text("Posibles") }
                )
            }

            // Contenido según pestaña seleccionada
            when (selectedTab) {
                0 -> RecipeList(
                    recipes = state.allRecipes,
                    isLoading = state.isLoading,
                    error = state.error,
                    onRecipeClick = onRecipeClick,
                    onFavoriteClick = { recipe ->
                        viewModel.toggleFavorite(recipe)
                    }
                )
                1 -> RecipeList(
                    recipes = state.availableRecipes,
                    isLoading = state.isLoading,
                    error = state.error,
                    onRecipeClick = onRecipeClick,
                    onFavoriteClick = { recipe ->
                        viewModel.toggleFavorite(recipe)
                    },
                    showMatchingPercentage = false
                )
                2 -> RecipeList(
                    recipes = state.possibleRecipes,
                    isLoading = state.isLoading,
                    error = state.error,
                    onRecipeClick = onRecipeClick,
                    onFavoriteClick = { recipe ->
                        viewModel.toggleFavorite(recipe)
                    },
                    showMatchingPercentage = true
                )
            }
        }
    }
}

@Composable
private fun RecipeList(
    recipes: List<com.example.shelfieapp.features.recipes.domain.model.Recipe>,
    isLoading: Boolean,
    error: String?,
    onRecipeClick: (String) -> Unit,
    onFavoriteClick: (com.example.shelfieapp.features.recipes.domain.model.Recipe) -> Unit,
    showMatchingPercentage: Boolean = false
) {
    if (isLoading) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    } else if (error != null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = error, color = MaterialTheme.colorScheme.error)
        }
    } else if (recipes.isEmpty()) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("No hay recetas disponibles")
                Text(
                    "Agrega ingredientes a tu despensa para ver recetas",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(recipes) { recipe ->
                RecipeCard(
                    recipe = recipe,
                    onClick = { onRecipeClick(recipe.id) },
                    onFavoriteClick = { onFavoriteClick(recipe) },
                    matchingPercentage = if (showMatchingPercentage) 50 else null // TODO: Calcular porcentaje real
                )
            }
        }
    }
}