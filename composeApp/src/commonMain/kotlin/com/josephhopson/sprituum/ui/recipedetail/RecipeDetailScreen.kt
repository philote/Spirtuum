package com.josephhopson.sprituum.ui.recipedetail

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.josephhopson.sprituum.domain.model.Recipe
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

/**
 * Composable for the recipe detail screen
 *
 * @param recipeId ID of the recipe to display
 * @param onNavigateBack Callback for navigating back to the recipe list
 * @param onNavigateToEdit Callback for navigating to edit screen
 * @param onShareRecipe Callback for sharing the recipe
 * @param viewModel ViewModel for the recipe detail screen
 */
@Composable
fun RecipeDetailScreen(
    recipeId: Long,
    onNavigateBack: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    onShareRecipe: (Recipe) -> Unit,
    viewModel: RecipeDetailViewModel = koinInject { parametersOf(recipeId) }
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()

    // Handle navigation events
    LaunchedEffect(navigationEvent) {
        navigationEvent?.let { event ->
            when (event) {
                is RecipeDetailNavigationEvent.NavigateBack -> onNavigateBack()
                is RecipeDetailNavigationEvent.NavigateToEdit -> onNavigateToEdit(event.recipeId)
                is RecipeDetailNavigationEvent.ShareRecipe -> onShareRecipe(event.recipe)
            }
            viewModel.consumeNavigationEvent()
        }
    }

    Scaffold(
        topBar = {
            RecipeDetailTopBar(
                recipeName = uiState.recipe?.name ?: "",
                isFavorite = uiState.recipe?.favorite ?: false,
                onNavigateBack = { viewModel.onEvent(RecipeDetailUiEvent.NavigateBack) },
                onToggleFavorite = { viewModel.onEvent(RecipeDetailUiEvent.ToggleFavorite) },
                onEditRecipe = { viewModel.onEvent(RecipeDetailUiEvent.EditRecipe) },
                onDeleteRecipe = { viewModel.onEvent(RecipeDetailUiEvent.DeleteRecipe) },
                onShareRecipe = { viewModel.onEvent(RecipeDetailUiEvent.ShareRecipe) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag("loading_indicator")
                    )
                }

                uiState.error != null -> {
                    ErrorMessage(error = uiState.error!!)
                }

                uiState.recipe != null -> {
                    RecipeDetailContent(recipe = uiState.recipe!!)
                }
            }
        }
    }
}

/**
 * Top app bar for the recipe detail screen
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeDetailTopBar(
    recipeName: String,
    isFavorite: Boolean,
    onNavigateBack: () -> Unit,
    onToggleFavorite: () -> Unit,
    onEditRecipe: () -> Unit,
    onDeleteRecipe: () -> Unit,
    onShareRecipe: () -> Unit
) {
    TopAppBar(
        title = {
            Text(
                text = recipeName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        },
        navigationIcon = {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier.semantics { contentDescription = "Navigate back" }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = null
                )
            }
        },
        actions = {
            IconButton(
                onClick = onToggleFavorite,
                modifier = Modifier.semantics {
                    contentDescription = if (isFavorite) "Remove from favorites" else "Add to favorites"
                }
            ) {
                Icon(
                    imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                    contentDescription = null
                )
            }
            IconButton(
                onClick = onShareRecipe,
                modifier = Modifier.semantics { contentDescription = "Share recipe" }
            ) {
                Icon(
                    imageVector = Icons.Default.Share,
                    contentDescription = null
                )
            }
            IconButton(
                onClick = onEditRecipe,
                modifier = Modifier.semantics { contentDescription = "Edit recipe" }
            ) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = null
                )
            }
            IconButton(
                onClick = onDeleteRecipe,
                modifier = Modifier.semantics { contentDescription = "Delete recipe" }
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = null
                )
            }
        }
    )
}

/**
 * Content for the recipe detail screen
 */
@Composable
private fun RecipeDetailContent(recipe: Recipe) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        // Recipe header
        if (recipe.altName != null) {
            Text(
                text = recipe.altName,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
        }

        Row {
            Text(
                text = if (recipe.alcoholic) "Alcoholic" else "Non-alcoholic",
                style = MaterialTheme.typography.bodyMedium
            )
            if (recipe.glassware != null) {
                Text(
                    text = " • ",
                    style = MaterialTheme.typography.bodyMedium
                )
                Text(
                    text = recipe.glassware,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        // Tags
        if (recipe.tags.isNotEmpty()) {
            Spacer(modifier = Modifier.height(12.dp))
            Row(modifier = Modifier.fillMaxWidth()) {
                recipe.tags.forEach { tag ->
                    Text(
                        text = "#$tag",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.padding(end = 8.dp)
                    )
                }
            }
        }

        // About section
        if (recipe.about != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "About",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = recipe.about,
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
        }

        // Ingredients section
        if (recipe.ingredients.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Ingredients",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            recipe.ingredients.forEach { ingredient ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    // Amount
                    ingredient.amount?.let { amount ->
                        Text(
                            text = "${amount.value} ${amount.label}",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.width(80.dp)
                        )
                    }

                    // Name
                    Column {
                        Text(
                            text = ingredient.name,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium
                        )

                        // Notes
                        ingredient.notes?.let { notes ->
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = notes,
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
        }

        // Instructions section
        if (recipe.instructions.isNotEmpty()) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Instructions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            recipe.instructions.forEach { instruction ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                ) {
                    Text(
                        text = "${instruction.step}.",
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.width(32.dp)
                    )
                    Text(
                        text = instruction.value,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            HorizontalDivider()
        }

        // Notes section
        if (recipe.notes != null) {
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Notes",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = recipe.notes,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        // Bottom spacer for better scrolling
        Spacer(modifier = Modifier.height(24.dp))
    }
}

/**
 * Error message display
 */
@Composable
private fun ErrorMessage(error: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = androidx.compose.foundation.layout.Arrangement.Center
    ) {
        Text(
            text = "⚠️ Error",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.error
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = error,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.error
        )
    }
}