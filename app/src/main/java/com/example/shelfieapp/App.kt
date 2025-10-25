package com.example.shelfieapp

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.shelfieapp.navigation.NavGraph
import com.example.shelfieapp.ui.theme.ShelfieAppTheme

@Composable
fun App() {
    ShelfieAppTheme { // ← Esto aplica tu tema
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            NavGraph() // Tu navegación principal
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AppPreview() {
    ShelfieAppTheme {
        App()
    }
}