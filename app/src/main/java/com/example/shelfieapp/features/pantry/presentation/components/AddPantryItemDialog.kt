package com.example.shelfieapp.features.pantry.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.foundation.text.KeyboardOptions

@Composable
fun AddPantryItemDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String, String?) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var quantity by remember { mutableStateOf("") }
    var unit by remember { mutableStateOf("gramos") }
    var expanded by remember { mutableStateOf(false) }

    val units = listOf(
        "gramos", "kilogramos", "litros", "mililitros",
        "unidades", "paquetes", "latas", "botellas"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Agregar ingrediente") },
        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                // Nombre
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Nombre") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )

                // Unidad - usando DropdownMenu regular
                Box(modifier = Modifier.fillMaxWidth()) {
                    OutlinedTextField(
                        value = unit,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unidad") },
                        trailingIcon = {
                            Icon(
                                imageVector = Icons.Default.ArrowDropDown,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { expanded = true },
                        singleLine = true
                    )

                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        modifier = Modifier.fillMaxWidth(0.8f)
                    ) {
                        units.forEach { unitOption ->
                            DropdownMenuItem(
                                text = { Text(unitOption) },
                                onClick = {
                                    unit = unitOption
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                // Cantidad - ahora en la posición donde estaba categoría
                OutlinedTextField(
                    value = quantity,
                    onValueChange = {
                        if (it.isEmpty() || it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            quantity = it
                        }
                    },
                    label = { Text("Cantidad") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Decimal
                    ),
                    singleLine = true
                )
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val quantityValue = quantity.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank() && quantityValue > 0) {
                        onConfirm(
                            name,
                            quantityValue,
                            unit,
                            null // ya no hay categoría
                        )
                        onDismiss()
                    }
                },
                enabled = name.isNotBlank() &&
                        (quantity.toDoubleOrNull() ?: 0.0) > 0
            ) {
                Text("Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}