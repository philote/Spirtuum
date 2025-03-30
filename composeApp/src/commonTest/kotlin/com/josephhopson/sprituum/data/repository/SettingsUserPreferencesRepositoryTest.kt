package com.josephhopson.sprituum.data.repository

import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.FilterOption
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.SortOption
import com.josephhopson.sprituum.domain.repository.UserPreferencesRepository.ViewMode
import com.russhwolf.settings.MapSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsUserPreferencesRepositoryTest {
    
    private lateinit var repository: UserPreferencesRepository
    private lateinit var mapSettings: MapSettings
    
    @BeforeTest
    fun setup() {
        mapSettings = MapSettings()
        repository = SettingsUserPreferencesRepository(mapSettings.toFlowSettings())
    }
    
    @Test
    fun testDefaultSortOption() = runTest {
        // When no sort option is set, it should default to NAME_ASC
        val sortOption = repository.getSortOption().first()
        assertEquals(SortOption.NAME_ASC, sortOption)
    }
    
    @Test
    fun testSetAndGetSortOption() = runTest {
        // Set a sort option
        repository.setSortOption(SortOption.DATE_CREATED_NEWEST)
        
        // Get the sort option and verify it matches what we set
        val sortOption = repository.getSortOption().first()
        assertEquals(SortOption.DATE_CREATED_NEWEST, sortOption)
    }
    
    @Test
    fun testDefaultFilterOption() = runTest {
        // When no filter option is set, it should default to ALL
        val filterOption = repository.getFilterOption().first()
        assertEquals(FilterOption.ALL, filterOption)
    }
    
    @Test
    fun testSetAndGetFilterOption() = runTest {
        // Set a filter option
        repository.setFilterOption(FilterOption.ALCOHOLIC)
        
        // Get the filter option and verify it matches what we set
        val filterOption = repository.getFilterOption().first()
        assertEquals(FilterOption.ALCOHOLIC, filterOption)
    }
    
    @Test
    fun testDefaultViewMode() = runTest {
        // When no view mode is set, it should default to LIST
        val viewMode = repository.getViewMode().first()
        assertEquals(ViewMode.LIST, viewMode)
    }
    
    @Test
    fun testSetAndGetViewMode() = runTest {
        // Set a view mode
        repository.setViewMode(ViewMode.GRID)
        
        // Get the view mode and verify it matches what we set
        val viewMode = repository.getViewMode().first()
        assertEquals(ViewMode.GRID, viewMode)
    }
}