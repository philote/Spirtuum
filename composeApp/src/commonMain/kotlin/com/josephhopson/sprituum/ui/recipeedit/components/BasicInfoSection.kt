package com.josephhopson.sprituum.ui.recipeedit.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Checkbox
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp

/**
 * Form section for basic recipe information
 */
@Composable
fun BasicInfoSection(
    name: String,
    onNameChange: (String) -> Unit,
    nameError: String?,
    altName: String,
    onAltNameChange: (String) -> Unit,
    glassware: String,
    onGlasswareChange: (String) -> Unit,
    garnish: String,
    onGarnishChange: (String) -> Unit,
    alcoholic: Boolean,
    onAlcoholicToggle: () -> Unit,
    favorite: Boolean,
    onFavoriteToggle: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.fillMaxWidth()) {
        // Recipe name - required
        OutlinedTextField(
            value = name,
            onValueChange = onNameChange,
            label = { Text("Recipe Name*") },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Recipe name field" },
            isError = nameError != null,
            supportingText = nameError?.let { { Text(it) } }
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Alternate name - optional
        OutlinedTextField(
            value = altName,
            onValueChange = onAltNameChange,
            label = { Text("Alternate Name (Optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Alternate name field" },
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Glassware - optional
        OutlinedTextField(
            value = glassware,
            onValueChange = onGlasswareChange,
            label = { Text("Glassware (Optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Glassware field" },
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Garnish - optional
        OutlinedTextField(
            value = garnish,
            onValueChange = onGarnishChange,
            label = { Text("Garnish (Optional)") },
            modifier = Modifier
                .fillMaxWidth()
                .semantics { contentDescription = "Garnish field" },
        )
        Spacer(modifier = Modifier.height(16.dp))

        // Alcoholic & favorite
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = "Alcoholic toggle" }
            ) {
                Checkbox(
                    checked = alcoholic,
                    onCheckedChange = { onAlcoholicToggle() }
                )
                Text(
                    text = "Alcoholic",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(1f)
                    .semantics { contentDescription = "Favorite toggle" }
            ) {
                Checkbox(
                    checked = favorite,
                    onCheckedChange = { onFavoriteToggle() }
                )
                Text(
                    text = "Favorite",
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }
    }
}