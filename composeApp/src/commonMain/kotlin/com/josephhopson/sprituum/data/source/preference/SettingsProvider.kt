package com.josephhopson.sprituum.data.source.preference

import com.russhwolf.settings.coroutines.FlowSettings

/**
 * Interface for providing settings storage
 */
interface SettingsProvider {
    /**
     * Provides a FlowSettings instance for accessing user preferences
     */
    fun provideSettings(): FlowSettings
}