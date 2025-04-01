package com.josephhopson.sprituum.ui.recipelist

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.filled.Sort
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import com.josephhopson.sprituum.domain.model.Recipe
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.FilterOption
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.SortOption
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.ViewMode
import com.josephhopson.sprituum.ui.components.RecipeCard
import org.koin.compose.koinInject

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
            CompactRecipeListHeader(
                searchQuery = uiState.searchQuery,
                onSearchQueryChange = { viewModel.onEvent(RecipeListUiEvent.UpdateSearchQuery(it)) },
                onClearSearch = { viewModel.onEvent(RecipeListUiEvent.ClearSearch) },
                sortOption = uiState.sortOption,
                filterOption = uiState.filterOption,
                viewMode = uiState.viewMode,
                onSortOptionChanged = { viewModel.onEvent(RecipeListUiEvent.UpdateSortOption(it)) },
                onFilterOptionChanged = { viewModel.onEvent(RecipeListUiEvent.UpdateFilterOption(it)) },
                onViewModeChanged = { viewModel.onEvent(RecipeListUiEvent.UpdateViewMode(it)) }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { viewModel.onEvent(RecipeListUiEvent.CreateNewRecipe) },
                modifier = Modifier.semantics { contentDescription = "Add new recipe" }
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { paddingValues ->
        if (uiState.isLoading) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.Center)
                        .testTag("loading_indicator")
                )
            }
        } else if (uiState.error != null) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)) {
                ErrorMessage(error = uiState.error!!)
            }
        } else if (uiState.recipes.isEmpty()) {
            Box(modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)) {
                EmptyRecipesList(
                    searchActive = uiState.searchQuery.isNotEmpty(),
                    onCreateRecipe = { viewModel.onEvent(RecipeListUiEvent.CreateNewRecipe) }
                )
            }
        } else {
            when (uiState.viewMode) {
                ViewMode.LIST -> RecipeListView(
                    recipes = uiState.recipes,
                    onRecipeClick = { viewModel.onEvent(RecipeListUiEvent.SelectRecipe(it)) },
                    onToggleFavorite = { viewModel.onEvent(RecipeListUiEvent.ToggleFavorite(it)) },
                    contentPadding = paddingValues
                )
                ViewMode.GRID -> RecipeGridView(
                    recipes = uiState.recipes,
                    onRecipeClick = { viewModel.onEvent(RecipeListUiEvent.SelectRecipe(it)) },
                    onToggleFavorite = { viewModel.onEvent(RecipeListUiEvent.ToggleFavorite(it)) },
                    contentPadding = paddingValues
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CompactRecipeListHeader(
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onClearSearch: () -> Unit,
    sortOption: SortOption,
    filterOption: FilterOption,
    viewMode: ViewMode,
    onSortOptionChanged: (SortOption) -> Unit,
    onFilterOptionChanged: (FilterOption) -> Unit,
    onViewModeChanged: (ViewMode) -> Unit
) {
    var showSearch by remember { mutableStateOf(searchQuery.isNotEmpty()) }
    var showSortMenu by remember { mutableStateOf(false) }
    var showFilterMenu by remember { mutableStateOf(false) }
    
    Surface(
        tonalElevation = 3.dp,
        color = MaterialTheme.colorScheme.surface
    ) {
        if (showSearch) {
            @Suppress("DEPRECATION")
            DockedSearchBar(
                query = searchQuery,
                onQueryChange = onSearchQueryChange,
                onSearch = { onSearchQueryChange(it) },
                active = false,
                onActiveChange = { },
                placeholder = { Text("Search recipes...") },
                leadingIcon = {
                    IconButton(onClick = {
                        onClearSearch()
                        showSearch = false
                    }) {
                        Icon(Icons.Filled.Close, contentDescription = "Close search")
                    }
                },
                trailingIcon = {
                    Row {
                        // Sort button with dropdown menu
                        Box {
                            IconButton(
                                onClick = { showSortMenu = true },
                                modifier = Modifier.semantics { contentDescription = "Sort recipes" }
                            ) {
                                Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = null)
                            }

                            SortDropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false },
                                sortOption = sortOption,
                                onSortOptionChanged = {
                                    onSortOptionChanged(it)
                                    showSortMenu = false
                                }
                            )
                        }

                        // Filter button with dropdown menu
                        Box {
                            IconButton(
                                onClick = { showFilterMenu = true },
                                modifier = Modifier.semantics { contentDescription = "Filter recipes" }
                            ) {
                                Icon(Icons.Filled.FilterList, contentDescription = null)
                            }

                            FilterDropdownMenu(
                                expanded = showFilterMenu,
                                onDismissRequest = { showFilterMenu = false },
                                filterOption = filterOption,
                                onFilterOptionChanged = {
                                    onFilterOptionChanged(it)
                                    showFilterMenu = false
                                }
                            )
                        }

                        // View mode toggle
                        IconButton(
                            onClick = {
                                onViewModeChanged(
                                    if (viewMode == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST
                                )
                            },
                            modifier = Modifier.semantics {
                                contentDescription = if (viewMode == ViewMode.LIST)
                                    "Switch to grid view"
                                else
                                    "Switch to list view"
                            }
                        ) {
                            Icon(
                                imageVector = if (viewMode == ViewMode.LIST)
                                    Icons.Filled.Dashboard
                                else
                                    Icons.AutoMirrored.Filled.List,
                                contentDescription = null
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                colors = SearchBarDefaults.colors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) { }
        } else {
            TopAppBar(
                title = { Text("Recipes") },
                actions = {
                    // Search button
                    IconButton(
                        onClick = { showSearch = true },
                        modifier = Modifier.semantics { contentDescription = "Search recipes" }
                    ) {
                        Icon(Icons.Filled.Search, contentDescription = null)
                    }
                    
                    // Sort button with dropdown menu
                    Box {
                        IconButton(
                            onClick = { showSortMenu = true },
                            modifier = Modifier.semantics { contentDescription = "Sort recipes" }
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Sort, contentDescription = null)
                        }
                        
                        SortDropdownMenu(
                            expanded = showSortMenu,
                            onDismissRequest = { showSortMenu = false },
                            sortOption = sortOption,
                            onSortOptionChanged = { 
                                onSortOptionChanged(it)
                                showSortMenu = false
                            }
                        )
                    }
                    
                    // Filter button with dropdown menu
                    Box {
                        IconButton(
                            onClick = { showFilterMenu = true },
                            modifier = Modifier.semantics { contentDescription = "Filter recipes" }
                        ) {
                            Icon(Icons.Filled.FilterList, contentDescription = null)
                        }
                        
                        FilterDropdownMenu(
                            expanded = showFilterMenu,
                            onDismissRequest = { showFilterMenu = false },
                            filterOption = filterOption,
                            onFilterOptionChanged = { 
                                onFilterOptionChanged(it)
                                showFilterMenu = false
                            }
                        )
                    }
                    
                    // View mode toggle
                    IconButton(
                        onClick = { 
                            onViewModeChanged(
                                if (viewMode == ViewMode.LIST) ViewMode.GRID else ViewMode.LIST
                            )
                        },
                        modifier = Modifier.semantics { 
                            contentDescription = if (viewMode == ViewMode.LIST) 
                                "Switch to grid view" 
                            else 
                                "Switch to list view"
                        }
                    ) {
                        Icon(
                            imageVector = if (viewMode == ViewMode.LIST)
                                Icons.Filled.Dashboard
                            else
                                Icons.AutoMirrored.Filled.List,
                            contentDescription = null
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    }
}

@Composable
private fun SortDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    sortOption: SortOption,
    onSortOptionChanged: (SortOption) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        DropdownMenuItem(
            text = { Text("Name (A-Z)") },
            onClick = { onSortOptionChanged(SortOption.NAME_ASC) },
            trailingIcon = {
                if (sortOption == SortOption.NAME_ASC) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        DropdownMenuItem(
            text = { Text("Name (Z-A)") },
            onClick = { onSortOptionChanged(SortOption.NAME_DESC) },
            trailingIcon = {
                if (sortOption == SortOption.NAME_DESC) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        DropdownMenuItem(
            text = { Text("Newest") },
            onClick = { onSortOptionChanged(SortOption.DATE_CREATED_NEWEST) },
            trailingIcon = {
                if (sortOption == SortOption.DATE_CREATED_NEWEST) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        DropdownMenuItem(
            text = { Text("Oldest") },
            onClick = { onSortOptionChanged(SortOption.DATE_CREATED_OLDEST) },
            trailingIcon = {
                if (sortOption == SortOption.DATE_CREATED_OLDEST) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        DropdownMenuItem(
            text = { Text("Last updated") },
            onClick = { onSortOptionChanged(SortOption.DATE_UPDATED_NEWEST) },
            trailingIcon = {
                if (sortOption == SortOption.DATE_UPDATED_NEWEST) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
    }
}

@Composable
private fun FilterDropdownMenu(
    expanded: Boolean,
    onDismissRequest: () -> Unit,
    filterOption: FilterOption,
    onFilterOptionChanged: (FilterOption) -> Unit
) {
    DropdownMenu(
        expanded = expanded,
        onDismissRequest = onDismissRequest
    ) {
        DropdownMenuItem(
            text = { Text("All") },
            onClick = { onFilterOptionChanged(FilterOption.ALL) },
            trailingIcon = {
                if (filterOption == FilterOption.ALL) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        DropdownMenuItem(
            text = { Text("Favorites") },
            onClick = { onFilterOptionChanged(FilterOption.FAVORITES) },
            trailingIcon = {
                if (filterOption == FilterOption.FAVORITES) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        DropdownMenuItem(
            text = { Text("Alcoholic") },
            onClick = { onFilterOptionChanged(FilterOption.ALCOHOLIC) },
            trailingIcon = {
                if (filterOption == FilterOption.ALCOHOLIC) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
        DropdownMenuItem(
            text = { Text("Non-alcoholic") },
            onClick = { onFilterOptionChanged(FilterOption.NON_ALCOHOLIC) },
            trailingIcon = {
                if (filterOption == FilterOption.NON_ALCOHOLIC) {
                    Icon(
                        imageVector = Icons.Filled.Check,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        )
    }
}

@Composable
private fun RecipeListView(
    recipes: List<Recipe>,
    onRecipeClick: (Long) -> Unit,
    onToggleFavorite: (Long) -> Unit,
    contentPadding: PaddingValues
) {
    LazyColumn(
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding(),
            start = 16.dp,
            end = 16.dp,
            bottom = contentPadding.calculateBottomPadding() + 16.dp
        ),
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
    onToggleFavorite: (Long) -> Unit,
    contentPadding: PaddingValues
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 300.dp),
        contentPadding = PaddingValues(
            top = contentPadding.calculateTopPadding(),
            start = 16.dp,
            end = 16.dp,
            bottom = contentPadding.calculateBottomPadding() + 16.dp
        ),
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
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(modifier = Modifier.width(8.dp))
                Text("Create Recipe")
            }
        }
    }
}