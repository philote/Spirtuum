package com.josephhopson.sprituum.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Filter chip component for the recipe app
 *
 * @param text Text to display in the chip
 * @param selected Whether the chip is selected
 * @param onClick Callback when the chip is clicked
 * @param modifier Modifier for the chip
 */
@Composable
fun FilterChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val backgroundColor by animateColorAsState(
        targetValue = if (selected)
            MaterialTheme.colorScheme.primary
        else
            MaterialTheme.colorScheme.surface,
        label = "Chip background color"
    )

    val textColor by animateColorAsState(
        targetValue = if (selected)
            MaterialTheme.colorScheme.onPrimary
        else
            MaterialTheme.colorScheme.primary,
        label = "Chip text color"
    )

    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge
            )
        },
        modifier = modifier
            .padding(end = 8.dp)
            .semantics {
                contentDescription = if (selected) {
                    "Selected: $text"
                } else {
                    "Filter by: $text"
                }
            },
        shape = RoundedCornerShape(8.dp),
        border = if (!selected) {
            BorderStroke(
                width = 1.dp,
                color = MaterialTheme.colorScheme.primary
            )
        } else null,
        colors = FilterChipDefaults.filterChipColors(
            containerColor = backgroundColor,
            labelColor = textColor,
            selectedContainerColor = backgroundColor,
            selectedLabelColor = textColor
        )
    )
}
