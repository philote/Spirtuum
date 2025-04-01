package com.josephhopson.sprituum.ui.recipeedit.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.josephhopson.sprituum.ui.recipeedit.IngredientUiState

/**
 * Form section for recipe ingredients
 */
@Composable
fun IngredientsSection(
    ingredients: List<IngredientUiState>,
    onAddIngredient: () -> Unit,
    onUpdateName: (Int, String) -> Unit,
    onUpdateAmount: (Int, Double?) -> Unit,
    onUpdateUnit: (Int, String) -> Unit,
    onUpdateNotes: (Int, String) -> Unit,
    onMoveUp: (Int) -> Unit,
    onMoveDown: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    error: String?,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Ingredients",
                style = MaterialTheme.typography.titleMedium
            )
            
            Button(
                onClick = onAddIngredient,
                modifier = Modifier.semantics { contentDescription = "Add ingredient button" }
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Ingredient")
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        error?.let {
            Text(
                text = it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, bottom = 8.dp)
            )
        }
        
        if (ingredients.isEmpty()) {
            Text(
                text = "No ingredients added yet",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(ingredients) { index, ingredient ->
                    IngredientItem(
                        ingredient = ingredient,
                        onUpdateName = { onUpdateName(index, it) },
                        onUpdateAmount = { onUpdateAmount(index, it) },
                        onUpdateUnit = { onUpdateUnit(index, it) },
                        onUpdateNotes = { onUpdateNotes(index, it) },
                        onMoveUp = { onMoveUp(index) },
                        onMoveDown = { onMoveDown(index) },
                        onDelete = { onDelete(index) },
                        isFirst = index == 0,
                        isLast = index == ingredients.lastIndex
                    )
                }
            }
        }
    }
}

/**
 * Individual ingredient item form
 */
@Composable
private fun IngredientItem(
    ingredient: IngredientUiState,
    onUpdateName: (String) -> Unit,
    onUpdateAmount: (Double?) -> Unit,
    onUpdateUnit: (String) -> Unit,
    onUpdateNotes: (String) -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onDelete: () -> Unit,
    isFirst: Boolean,
    isLast: Boolean
) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            // Ingredient name - required
            OutlinedTextField(
                value = ingredient.name,
                onValueChange = onUpdateName,
                label = { Text("Ingredient Name*") },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Ingredient name field" },
                singleLine = true
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Amount and unit
            Row(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = ingredient.amount?.toString() ?: "",
                    onValueChange = { 
                        val amount = it.toDoubleOrNull()
                        onUpdateAmount(amount)
                    },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier
                        .weight(1f)
                        .semantics { contentDescription = "Ingredient amount field" },
                    singleLine = true
                )
                
                Spacer(modifier = Modifier.width(8.dp))
                
                OutlinedTextField(
                    value = ingredient.unit,
                    onValueChange = onUpdateUnit,
                    label = { Text("Unit") },
                    modifier = Modifier
                        .weight(1f)
                        .semantics { contentDescription = "Ingredient unit field" },
                    singleLine = true
                )
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Notes
            OutlinedTextField(
                value = ingredient.notes,
                onValueChange = onUpdateNotes,
                label = { Text("Notes (Optional)") },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Ingredient notes field" },
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            // Action buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                IconButton(
                    onClick = onMoveUp,
                    enabled = !isFirst,
                    modifier = Modifier.semantics { contentDescription = "Move ingredient up" }
                ) {
                    Icon(Icons.Filled.KeyboardArrowUp, contentDescription = null)
                }
                
                IconButton(
                    onClick = onMoveDown,
                    enabled = !isLast,
                    modifier = Modifier.semantics { contentDescription = "Move ingredient down" }
                ) {
                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null)
                }
                
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.semantics { contentDescription = "Delete ingredient" }
                ) {
                    Icon(
                        Icons.Filled.Delete,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}