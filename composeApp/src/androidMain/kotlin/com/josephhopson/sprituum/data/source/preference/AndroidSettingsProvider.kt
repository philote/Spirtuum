package com.josephhopson.sprituum.data.source.preference

import com.russhwolf.settings.coroutines.FlowSettings
import com.russhwolf.settings.coroutines.toFlowSettings
import android.content.Context
import android.content.SharedPreferences
import com.russhwolf.settings.SharedPreferencesSettings

/**
 * Android-specific implementation of settings provider using SharedPreferences
 */
class AndroidSettingsProvider(context: Context) : SettingsProvider {
    
    private val sharedPreferences: SharedPreferences = 
        context.getSharedPreferences("spirituum_preferences", Context.MODE_PRIVATE)
    
    override fun provideSettings(): FlowSettings {
        val settings = SharedPreferencesSettings(sharedPreferences)
        return settings.toFlowSettings()
    }
}