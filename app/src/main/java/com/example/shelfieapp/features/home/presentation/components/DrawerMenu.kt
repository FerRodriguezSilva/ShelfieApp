package com.example.shelfieapp.features.home.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
fun DrawerMenu(
    drawerState: DrawerState,
    coroutineScope: CoroutineScope,
    onItemClick: (String) -> Unit,
    content: @Composable () -> Unit
) {
    val menuItems = listOf(
        "🏠 Inicio" to "home",
        "🧺 Mi despensa" to "pantry",
        "🍲 Recetas" to "recipes",
        "🛒 Lista de compras" to "shopping",
        "⚙️ Configuración" to "settings"
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(16.dp)
                ) {
                    Text(
                        text = "DespensaApp",
                        style = androidx.compose.material3.MaterialTheme.typography.headlineMedium,
                        modifier = Modifier.padding(bottom = 32.dp)
                    )

                    menuItems.forEach { (title, route) ->
                        NavigationDrawerItem(
                            label = { Text(text = title) },
                            selected = false,
                            onClick = {
                                coroutineScope.launch { drawerState.close() }
                                onItemClick(route)
                            },
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                }
            }
        },
        content = content
    )
}