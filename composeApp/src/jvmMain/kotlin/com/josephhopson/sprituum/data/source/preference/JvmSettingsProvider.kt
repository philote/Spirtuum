package com.josephhopson.sprituum.data.source.preference

import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import com.russhwolf.settings.PreferencesSettings
import java.util.prefs.Preferences

/**
 * JVM implementation of settings provider using Java Preferences API
 */
class JvmSettingsProvider : SettingsProvider {

    private val preferences: Preferences =
        Preferences.userRoot().node("com.josephhopson.sprituum")

    override fun provideSettings(): FlowSettings {
        val settings = PreferencesSettings(preferences)
        return settings.toFlowSettings()
    }
}