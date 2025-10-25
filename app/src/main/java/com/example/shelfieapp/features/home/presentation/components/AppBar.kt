package com.example.shelfieapp.features.home.presentation.components

import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import com.example.shelfieapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBar(
    onMenuClick: () -> Unit,
    onSearchClick: () -> Unit
) {
    CenterAlignedTopAppBar(
        title = {
            Text(
                text = "DespensaApp",
                style = MaterialTheme.typography.headlineSmall
            )
        },
        navigationIcon = {
            IconButton(onClick = onMenuClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_menu), // Necesitarás este icono
                    contentDescription = "Menú"
                )
            }
        },
        actions = {
            IconButton(onClick = onSearchClick) {
                Icon(
                    imageVector = ImageVector.vectorResource(id = R.drawable.ic_search), // Necesitarás este icono
                    contentDescription = "Buscar"
                )
            }
        }
    )
}