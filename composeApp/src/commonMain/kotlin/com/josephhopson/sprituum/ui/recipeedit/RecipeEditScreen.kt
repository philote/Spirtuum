package com.josephhopson.sprituum.ui.recipeedit

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.josephhopson.sprituum.ui.common.CompactHeader
import com.josephhopson.sprituum.ui.common.ErrorScreen
import com.josephhopson.sprituum.ui.recipeedit.components.AboutSection
import com.josephhopson.sprituum.ui.recipeedit.components.BasicInfoSection
import com.josephhopson.sprituum.ui.recipeedit.components.ImageSection
import com.josephhopson.sprituum.ui.recipeedit.components.IngredientsSection
import com.josephhopson.sprituum.ui.recipeedit.components.InstructionsSection
import com.josephhopson.sprituum.ui.recipeedit.components.NotesSection
import com.josephhopson.sprituum.ui.recipeedit.components.TagsSection

/**
 * Screen for creating or editing a recipe
 */
@Composable
fun RecipeEditScreen(
    viewModel: RecipeEditViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToDetail: (Long) -> Unit,
    onCapturePhoto: () -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    // Handle navigation events
    LaunchedEffect(navigationEvent) {
        navigationEvent?.let {
            when (it) {
                RecipeEditNavigationEvent.NavigateBack -> onNavigateBack()
                is RecipeEditNavigationEvent.NavigateToDetail -> onNavigateToDetail(it.recipeId)
                RecipeEditNavigationEvent.CapturePhoto -> onCapturePhoto()
            }
            viewModel.consumeNavigationEvent()
        }
    }

    // Handle errors in UI
    LaunchedEffect(uiState.error) {
        uiState.error?.let {
            snackbarHostState.showSnackbar(it)
        }
    }

    Scaffold(
        topBar = {
            CompactHeader(
                title = if (uiState.isNewRecipe) "Create Recipe" else "Edit Recipe",
                onNavigateBack = { viewModel.onEvent(RecipeEditUiEvent.Cancel) },
                actions = {
                    // No additional actions in the original header
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(RecipeEditUiEvent.SaveRecipe) },
                modifier = Modifier.semantics { contentDescription = "Save recipe" },
                containerColor = MaterialTheme.colorScheme.primary
            ) {
                Icon(Icons.Filled.Check, contentDescription = null)
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        // Content
        if (uiState.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null && uiState.id == 0L) {
            // Only show error screen for critical errors (not validation errors)
            ErrorScreen(
                message = uiState.error ?: "Unknown error",
                onRetry = { viewModel.onEvent(RecipeEditUiEvent.Cancel) }
            )
        } else {
            // Form content
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 16.dp)
            ) {
                // Image section
                ImageSection(
                    imagePath = uiState.imagePath,
                    onTakePhoto = { viewModel.onEvent(RecipeEditUiEvent.TakePhoto) },
                    onRemovePhoto = { viewModel.onEvent(RecipeEditUiEvent.RemovePhoto) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Basic info section
                BasicInfoSection(
                    name = uiState.name,
                    onNameChange = { viewModel.onEvent(RecipeEditUiEvent.UpdateName(it)) },
                    nameError = uiState.validationErrors["name"],
                    altName = uiState.altName,
                    onAltNameChange = { viewModel.onEvent(RecipeEditUiEvent.UpdateAltName(it)) },
                    glassware = uiState.glassware,
                    onGlasswareChange = { viewModel.onEvent(RecipeEditUiEvent.UpdateGlassware(it)) },
                    garnish = uiState.garnish,
                    onGarnishChange = { viewModel.onEvent(RecipeEditUiEvent.UpdateGarnish(it)) },
                    alcoholic = uiState.alcoholic,
                    onAlcoholicToggle = { viewModel.onEvent(RecipeEditUiEvent.ToggleAlcoholic) },
                    favorite = uiState.favorite,
                    onFavoriteToggle = { viewModel.onEvent(RecipeEditUiEvent.ToggleFavorite) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // About section
                AboutSection(
                    about = uiState.about,
                    onAboutChange = { viewModel.onEvent(RecipeEditUiEvent.UpdateAbout(it)) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Tags section
                TagsSection(
                    tags = uiState.tags,
                    onDeleteTag = { viewModel.onEvent(RecipeEditUiEvent.DeleteTag(it)) },
                    newTag = uiState.newTag,
                    onNewTagChange = { viewModel.onEvent(RecipeEditUiEvent.UpdateNewTag(it)) },
                    onAddTag = { viewModel.onEvent(RecipeEditUiEvent.AddTag) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Ingredients section
                IngredientsSection(
                    ingredients = uiState.ingredients,
                    onAddIngredient = { viewModel.onEvent(RecipeEditUiEvent.AddIngredient()) },
                    onUpdateName = { index, value ->
                        viewModel.onEvent(
                            RecipeEditUiEvent.UpdateIngredientName(
                                index,
                                value
                            )
                        )
                    },
                    onUpdateAmount = { index, value ->
                        viewModel.onEvent(
                            RecipeEditUiEvent.UpdateIngredientAmount(
                                index,
                                value
                            )
                        )
                    },
                    onUpdateUnit = { index, value ->
                        viewModel.onEvent(
                            RecipeEditUiEvent.UpdateIngredientUnit(
                                index,
                                value
                            )
                        )
                    },
                    onUpdateNotes = { index, value ->
                        viewModel.onEvent(
                            RecipeEditUiEvent.UpdateIngredientNotes(
                                index,
                                value
                            )
                        )
                    },
                    onMoveUp = { index -> viewModel.onEvent(RecipeEditUiEvent.MoveIngredientUp(index)) },
                    onMoveDown = { index -> viewModel.onEvent(RecipeEditUiEvent.MoveIngredientDown(index)) },
                    onDelete = { index -> viewModel.onEvent(RecipeEditUiEvent.DeleteIngredient(index)) },
                    error = uiState.validationErrors["ingredients"]
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Instructions section
                InstructionsSection(
                    instructions = uiState.instructions,
                    onAddInstruction = { viewModel.onEvent(RecipeEditUiEvent.AddInstruction()) },
                    onUpdateValue = { index, value ->
                        viewModel.onEvent(
                            RecipeEditUiEvent.UpdateInstructionValue(
                                index,
                                value
                            )
                        )
                    },
                    onMoveUp = { index -> viewModel.onEvent(RecipeEditUiEvent.MoveInstructionUp(index)) },
                    onMoveDown = { index -> viewModel.onEvent(RecipeEditUiEvent.MoveInstructionDown(index)) },
                    onDelete = { index -> viewModel.onEvent(RecipeEditUiEvent.DeleteInstruction(index)) }
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Notes section
                NotesSection(
                    notes = uiState.notes,
                    onNotesChange = { viewModel.onEvent(RecipeEditUiEvent.UpdateNotes(it)) }
                )

                // Add extra space at bottom to ensure FAB doesn't obscure content
                Spacer(modifier = Modifier.height(80.dp))
            }
        }
    }
}