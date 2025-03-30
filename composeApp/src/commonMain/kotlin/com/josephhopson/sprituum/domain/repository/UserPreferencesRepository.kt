package com.josephhopson.sprituum.domain.repository

import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for user preferences
 *
 * This interface defines the contract for accessing and managing user preferences
 * such as sorting, filtering, and view mode settings.
 */
interface UserPreferencesRepository {
    /**
     * Sorting options for recipes
     */
    enum class SortOption {
        NAME_ASC,
        NAME_DESC,
        DATE_CREATED_NEWEST,
        DATE_CREATED_OLDEST,
        DATE_UPDATED_NEWEST,
        DATE_UPDATED_OLDEST
    }

    /**
     * Filter options for recipes
     */
    enum class FilterOption {
        ALL,
        FAVORITES,
        ALCOHOLIC,
        NON_ALCOHOLIC
    }

    /**
     * View mode for recipe list
     */
    enum class ViewMode {
        LIST,
        GRID
    }

    /**
     * Gets the current sort option
     * @return Flow of the current sort option
     */
    fun getSortOption(): Flow<SortOption>

    /**
     * Sets the sort option
     * @param sortOption The sort option to set
     */
    suspend fun setSortOption(sortOption: SortOption)

    /**
     * Gets the current filter option
     * @return Flow of the current filter option
     */
    fun getFilterOption(): Flow<FilterOption>

    /**
     * Sets the filter option
     * @param filterOption The filter option to set
     */
    suspend fun setFilterOption(filterOption: FilterOption)

    /**
     * Gets the current view mode
     * @return Flow of the current view mode
     */
    fun getViewMode(): Flow<ViewMode>

    /**
     * Sets the view mode
     * @param viewMode The view mode to set
     */
    suspend fun setViewMode(viewMode: ViewMode)
}