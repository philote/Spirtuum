package com.josephhopson.sprituum.ui.recipelist

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.josephhopson.sprituum.domain.model.Recipe
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.FilterOption
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.SortOption
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.ViewMode
import com.josephhopson.sprituum.ui.components.FilterChip
import com.josephhopson.sprituum.ui.components.RecipeCard
import org.koin.compose.koinInject

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeListScreen(
    viewModel: RecipeListViewModel = koinInject(),
    onNavigateToDetail: (Long) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()
    val navigationEvent by viewModel.navigationEvent.collectAsState()

    LaunchedEffect(navigationEvent) {
        navigationEvent?.let {
            onNavigateToDetail(it)
            viewModel.consumeNavigationEvent()
        }
    }

    Scaffold(
        topBar = {
            RecipeListTopBar(
                onCreateRecipeClick = { viewModel.onEvent(RecipeListUiEvent.CreateNewRecipe) },
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = { viewModel.onEvent(RecipeListUiEvent.UpdateSearchQuery(it)) },
                onClearSearch = { viewModel.onEvent(RecipeListUiEvent.ClearSearch) }
            )
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            RecipeListFilters(
                sortOption = uiState.sortOption,
                filterOption = uiState.filterOption,
                viewMode = uiState.viewMode,
                onSortOptionChanged = { viewModel.onEvent(RecipeListUiEvent.UpdateSortOption(it)) },
                onFilterOptionChanged = { viewModel.onEvent(RecipeListUiEvent.UpdateFilterOption(it)) },
                onViewModeChanged = { viewModel.onEvent(RecipeListUiEvent.UpdateViewMode(it)) },
                modifier = Modifier.fillMaxWidth()
            )

            if (uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize()) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.Center)
                            .testTag("loading_indicator")
                    )
                }
            } else if (uiState.error != null) {
                ErrorMessage(error = uiState.error!!)
            } else if (uiState.recipes.isEmpty()) {
                EmptyRecipesList(
                    searchActive = uiState.searchQuery.isNotEmpty(),
                    onCreateRecipe = { viewModel.onEvent(RecipeListUiEvent.CreateNewRecipe) }
                )
            } else {
                when (uiState.viewMode) {
                    ViewMode.LIST -> RecipeListView(
                        recipes = uiState.recipes,
                        onRecipeClick = { viewModel.onEvent(RecipeListUiEvent.SelectRecipe(it)) },
                        onToggleFavorite = { viewModel.onEvent(RecipeListUiEvent.ToggleFavorite(it)) }
                    )
                    ViewMode.GRID -> RecipeGridView(
                        recipes = uiState.recipes,
                        onRecipeClick = { viewModel.onEvent(RecipeListUiEvent.SelectRecipe(it)) },
                        onToggleFavorite = { viewModel.onEvent(RecipeListUiEvent.ToggleFavorite(it)) }
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeListTopBar(
    onCreateRecipeClick: () -> Unit,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit
) {
    var showSearch by remember { mutableStateOf(searchQuery.isNotEmpty()) }

    TopAppBar(
        title = {
            if (showSearch) {
                TextField(
                    value = searchQuery,
                    onValueChange = onSearchQueryChange,
                    placeholder = { Text("Search recipes...") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        disabledContainerColor = MaterialTheme.colorScheme.surface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        unfocusedIndicatorColor = Color.Transparent
                    )
                )
            } else {
                Text("Recipes")
            }
        },
        actions = {
            if (showSearch) {
                IconButton(
                    onClick = {
                        onClearSearch()
                        showSearch = false
                    },
                    modifier = Modifier.semantics { contentDescription = "Clear search" }
                ) {
                    Icon(Icons.Default.Close, contentDescription = null)
                }
            } else {
                IconButton(
                    onClick = { showSearch = true },
                    modifier = Modifier.semantics { contentDescription = "Search recipes" }
                ) {
                    Icon(Icons.Default.Search, contentDescription = null)
                }
            }
            IconButton(
                onClick = onCreateRecipeClick,
                modifier = Modifier.semantics { contentDescription = "Add new recipe" }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
            }
        }
    )
}

@Composable
private fun RecipeListFilters(
    sortOption: SortOption,
    filterOption: FilterOption,
    viewMode: ViewMode,
    onSortOptionChanged: (SortOption) -> Unit,
    onFilterOptionChanged: (FilterOption) -> Unit,
    onViewModeChanged: (ViewMode) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier.padding(horizontal = 16.dp)) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Sort & Filter",
                style = MaterialTheme.typography.titleMedium
            )

            Row {
                Button(
                    onClick = { onViewModeChanged(ViewMode.LIST) },
                    modifier = Modifier.semantics { contentDescription = "Switch to list view" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewMode == ViewMode.LIST)
                        MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text("List")
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = { onViewModeChanged(ViewMode.GRID) },
                    modifier = Modifier.semantics { contentDescription = "Switch to grid view" },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (viewMode == ViewMode.GRID)
                        MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text("Grid")
                }
            }
        }

        Text(
            "Sort by:",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 8.dp)
        ) {
            FilterChip(
                text = "Name (A-Z)",
                selected = sortOption == SortOption.NAME_ASC,
                onClick = { onSortOptionChanged(SortOption.NAME_ASC) }
            )
            FilterChip(
                text = "Name (Z-A)",
                selected = sortOption == SortOption.NAME_DESC,
                onClick = { onSortOptionChanged(SortOption.NAME_DESC) }
            )
            FilterChip(
                text = "Newest",
                selected = sortOption == SortOption.DATE_CREATED_NEWEST,
                onClick = { onSortOptionChanged(SortOption.DATE_CREATED_NEWEST) }
            )
            FilterChip(
                text = "Oldest",
                selected = sortOption == SortOption.DATE_CREATED_OLDEST,
                onClick = { onSortOptionChanged(SortOption.DATE_CREATED_OLDEST) }
            )
            FilterChip(
                text = "Last updated",
                selected = sortOption == SortOption.DATE_UPDATED_NEWEST,
                onClick = { onSortOptionChanged(SortOption.DATE_UPDATED_NEWEST) }
            )
        }

        Text(
            "Filter by:",
            style = MaterialTheme.typography.bodySmall,
            modifier = Modifier.padding(bottom = 4.dp)
        )

        Row(
            modifier = Modifier
                .horizontalScroll(rememberScrollState())
                .padding(bottom = 8.dp)
        ) {
            FilterChip(
                text = "All",
                selected = filterOption == FilterOption.ALL,
                onClick = { onFilterOptionChanged(FilterOption.ALL) }
            )
            FilterChip(
                text = "Favorites",
                selected = filterOption == FilterOption.FAVORITES,
                onClick = { onFilterOptionChanged(FilterOption.FAVORITES) }
            )
            FilterChip(
                text = "Alcoholic",
                selected = filterOption == FilterOption.ALCOHOLIC,
                onClick = { onFilterOptionChanged(FilterOption.ALCOHOLIC) }
            )
            FilterChip(
                text = "Non-alcoholic",
                selected = filterOption == FilterOption.NON_ALCOHOLIC,
                onClick = { onFilterOptionChanged(FilterOption.NON_ALCOHOLIC) }
            )
        }
    }
}

@Composable
private fun RecipeListView(
    recipes: List<Recipe>,
    onRecipeClick: (Long) -> Unit,
    onToggleFavorite: (Long) -> Unit
) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(recipes) { recipe ->
            RecipeCard(
                recipe = recipe,
                onClick = { onRecipeClick(recipe.id) },
                onFavoriteToggle = { onToggleFavorite(recipe.id) }
            )
        }
    }
}

@Composable
private fun RecipeGridView(
    recipes: List<Recipe>,
    onRecipeClick: (Long) -> Unit,
    onToggleFavorite: (Long) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 300.dp),
        contentPadding = PaddingValues(16.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        modifier = Modifier.fillMaxSize()
    ) {
        items(recipes) { recipe ->
            RecipeCard(
                recipe = recipe,
                onClick = { onRecipeClick(recipe.id) },
                onFavoriteToggle = { onToggleFavorite(recipe.id) }
            )
        }
    }
}

@Composable
private fun ErrorMessage(error: String) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
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

@Composable
private fun EmptyRecipesList(
    searchActive: Boolean,
    onCreateRecipe: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        if (searchActive) {
            Text(
                text = "🔍",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No recipes found matching your search",
                style = MaterialTheme.typography.bodyLarge
            )
        } else {
            Text(
                text = "🍹",
                style = MaterialTheme.typography.displayMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "No recipes yet",
                style = MaterialTheme.typography.titleLarge
            )
            Text(
                text = "Create your first cocktail recipe to get started",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onCreateRecipe,
                modifier = Modifier.semantics { contentDescription = "Create first recipe" }
            ) {
                Icon(Icons.Default.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Recipe")
            }
        }
    }
}