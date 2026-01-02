// Archivo: com/example/shelfieapp/features/pantry/presentation/components/AddPantryItemDialog.kt
package com.example.shelfieapp.features.pantry.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.example.shelfieapp.features.pantry.domain.model.PantryItem

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddPantryItemDialog(
    item: PantryItem? = null,
    onDismiss: () -> Unit,
    onConfirm: (String, Double, String, String?) -> Unit
) {
    var name by remember { mutableStateOf(item?.name ?: "") }
    var quantity by remember { mutableStateOf(item?.quantity?.toString() ?: "") }
    var unit by remember { mutableStateOf(item?.unit ?: "gramos") }
    var category by remember { mutableStateOf(item?.category ?: "") }
    var expanded by remember { mutableStateOf(false) }

    val units = listOf(
        "gramos", "kilogramos", "litros", "mililitros",
        "unidades", "paquetes", "latas", "botellas"
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (item != null) "Editar ingrediente" else "Agregar ingrediente") },
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

                // Cantidad
                OutlinedTextField(
                    value = quantity,
                    onValueChange = {
                        if (it.matches(Regex("^\\d*\\.?\\d*$"))) {
                            quantity = it
                        }
                    },
                    label = { Text("Cantidad") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    singleLine = true
                )

                // Unidad (SEPARADO → NO BUG)
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {

                    OutlinedTextField(
                        value = unit,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Unidad") },
                        trailingIcon = {
                            ExposedDropdownMenuDefaults.TrailingIcon(expanded)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .menuAnchor(),
                        singleLine = true
                    )

                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
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

                // Categoría
                OutlinedTextField(
                    value = category,
                    onValueChange = { category = it },
                    label = { Text("Categoría (opcional)") },
                    modifier = Modifier.fillMaxWidth(),
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
                            category.ifBlank { null }
                        )
                    }
                },
                enabled = name.isNotBlank() &&
                        (quantity.toDoubleOrNull() ?: 0.0) > 0
            ) {
                Text(if (item != null) "Guardar" else "Agregar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}