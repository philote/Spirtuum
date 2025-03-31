package com.josephhopson.sprituum.ui.components

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ElevatedFilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp

/**
 * A custom filter chip that shows a filter option
 *
 * @param text The text to display in the chip
 * @param selected Whether the chip is selected
 * @param onClick Callback for when the chip is clicked
 * @param modifier The modifier for the chip
 */
@Composable
fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val stateDescription = if (selected) "Selected" else "Not selected"
    val chipContentDescription = "$text filter. $stateDescription."

    ElevatedFilterChip(
        selected = selected,
        onClick = onClick,
        label = { Text(text) },
        modifier = modifier
            .padding(end = 8.dp)
            .semantics {
                contentDescription = chipContentDescription
                // State description helps screen readers understand the current state
                this.stateDescription = stateDescription
            },
        colors = FilterChipDefaults.elevatedFilterChipColors(
            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
        )
    )
}