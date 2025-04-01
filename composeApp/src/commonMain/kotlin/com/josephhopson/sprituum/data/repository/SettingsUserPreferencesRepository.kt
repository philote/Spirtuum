@file:OptIn(ExperimentalSettingsApi::class)

package com.josephhopson.sprituum.data.repository

import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.FilterOption
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.SortOption
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.ViewMode
import com.russhwolf.settings.ExperimentalSettingsApi
import com.russhwolf.settings.coroutines.FlowSettings
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Implementation of UserPreferencesRepository that uses MultiplatformSettings for storage
 */
class SettingsUserPreferencesRepository(
    private val settings: FlowSettings
) : UserPreferencesRepository {

    companion object {
        private const val KEY_SORT_OPTION = "sort_option"
        private const val KEY_FILTER_OPTION = "filter_option"
        private const val KEY_VIEW_MODE = "view_mode"
        
        // Default values
        private const val DEFAULT_SORT_OPTION = "NAME_ASC"
        private const val DEFAULT_FILTER_OPTION = "ALL"
        private const val DEFAULT_VIEW_MODE = "LIST"
        
        // First launch key
        private const val KEY_FIRST_LAUNCH = "first_launch"
        private const val DEFAULT_FIRST_LAUNCH = true
    }
    
    override fun getSortOption(): Flow<SortOption> {
        return settings.getStringOrNullFlow(KEY_SORT_OPTION)
            .map { value: String? ->
                value?.let { str: String -> SortOption.valueOf(str) } ?: SortOption.valueOf(DEFAULT_SORT_OPTION)
            }
    }
    
    override suspend fun setSortOption(sortOption: SortOption) {
        settings.putString(KEY_SORT_OPTION, sortOption.name)
    }
    
    override fun getFilterOption(): Flow<FilterOption> {
        return settings.getStringOrNullFlow(KEY_FILTER_OPTION)
            .map { value: String? ->
                value?.let { str: String -> FilterOption.valueOf(str) } ?: FilterOption.valueOf(DEFAULT_FILTER_OPTION)
            }
    }
    
    override suspend fun setFilterOption(filterOption: FilterOption) {
        settings.putString(KEY_FILTER_OPTION, filterOption.name)
    }
    
    override fun getViewMode(): Flow<ViewMode> {
        return settings.getStringOrNullFlow(KEY_VIEW_MODE)
            .map { value: String? ->
                value?.let { str: String -> ViewMode.valueOf(str) } ?: ViewMode.valueOf(DEFAULT_VIEW_MODE)
            }
    }
    
    override suspend fun setViewMode(viewMode: ViewMode) {
        settings.putString(KEY_VIEW_MODE, viewMode.name)
    }
    
    override fun isFirstLaunch(): Flow<Boolean> {
        return settings.getBooleanOrNullFlow(KEY_FIRST_LAUNCH)
            .map { value: Boolean? -> value ?: DEFAULT_FIRST_LAUNCH }
    }
    
    override suspend fun markFirstLaunchComplete() {
        settings.putBoolean(KEY_FIRST_LAUNCH, false)
    }
}
