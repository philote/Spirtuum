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
import androidx.compose.ui.unit.dp
import com.josephhopson.sprituum.ui.recipeedit.InstructionUiState

/**
 * Form section for recipe instructions
 */
@Composable
fun InstructionsSection(
    instructions: List<InstructionUiState>,
    onAddInstruction: () -> Unit,
    onUpdateValue: (Int, String) -> Unit,
    onMoveUp: (Int) -> Unit,
    onMoveDown: (Int) -> Unit,
    onDelete: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Instructions",
                style = MaterialTheme.typography.titleMedium
            )

            Button(
                onClick = onAddInstruction,
                modifier = Modifier.semantics { contentDescription = "Add instruction button" }
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add Step")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (instructions.isEmpty()) {
            Text(
                text = "No instructions added yet",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 16.dp)
            )
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                itemsIndexed(instructions) { index, instruction ->
                    InstructionItem(
                        instruction = instruction,
                        onUpdateValue = { onUpdateValue(index, it) },
                        onMoveUp = { onMoveUp(index) },
                        onMoveDown = { onMoveDown(index) },
                        onDelete = { onDelete(index) },
                        isFirst = index == 0,
                        isLast = index == instructions.lastIndex
                    )
                }
            }
        }
    }
}

/**
 * Individual instruction item form
 */
@Composable
private fun InstructionItem(
    instruction: InstructionUiState,
    onUpdateValue: (String) -> Unit,
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
            // Step number and label
            Text(
                text = "Step ${instruction.step}",
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Instruction value
            OutlinedTextField(
                value = instruction.value,
                onValueChange = onUpdateValue,
                label = { Text("Instruction") },
                modifier = Modifier
                    .fillMaxWidth()
                    .semantics { contentDescription = "Instruction step ${instruction.step} field" },
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
                    modifier = Modifier.semantics { contentDescription = "Move instruction up" }
                ) {
                    Icon(Icons.Filled.KeyboardArrowUp, contentDescription = null)
                }

                IconButton(
                    onClick = onMoveDown,
                    enabled = !isLast,
                    modifier = Modifier.semantics { contentDescription = "Move instruction down" }
                ) {
                    Icon(Icons.Filled.KeyboardArrowDown, contentDescription = null)
                }

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier.semantics { contentDescription = "Delete instruction" }
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