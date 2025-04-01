package com.josephhopson.sprituum.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

/**
 * Reusable compact header component that can be used across multiple screens
 *
 * @param title The title to display in the header
 * @param onNavigateBack Optional callback for navigation back action (adds a back button when provided)
 * @param actions Optional composable for action icons in the top right
 * @param isElevated Whether the header should have elevation/shadow
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CompactHeader(
    title: String,
    onNavigateBack: (() -> Unit)? = null,
    actions: @Composable () -> Unit = {},
    isElevated: Boolean = true
) {
    Surface(
        tonalElevation = if (isElevated) 3.dp else 0.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        TopAppBar(
            title = {
                Text(
                    text = title,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            },
            navigationIcon = {
                onNavigateBack?.let {
                    IconButton(
                        onClick = it,
                        modifier = Modifier.semantics { contentDescription = "Navigate back" }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = null)
                    }
                }
            },
            actions = { actions() },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.surface
            )
        )
    }
}