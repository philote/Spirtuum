package com.josephhopson.sprituum.data.source.recipes

import android.content.Context
import co.touchlab.kermit.Logger
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

private val logger = Logger.withTag("AndroidResourceLoader")

/**
 * Android implementation of resource loading
 */
actual suspend fun loadResourceAsString(path: String): String = withContext(Dispatchers.IO) {
    val resourceReader = AndroidResourceReader()
    logger.d { "Loading resource from path: $path" }
    resourceReader.readResource(path)
}

/**
 * Helper class to read resources on Android
 */
private class AndroidResourceReader : KoinComponent {
    private val context: Context by inject()

    /**
     * Reads a resource file from assets
     * For common resources, we should look in resources/ directory
     */
    fun readResource(path: String): String {
        return try {
            // First attempt to load from resources directory (where common resources are copied)
            val modifiedPath = if (!path.startsWith("resources/")) {
                "resources/$path"
            } else {
                path
            }

            logger.d { "Trying to load from modified path: $modifiedPath" }
            context.assets.open(modifiedPath).bufferedReader().use {
            val content = it.readText()
                logger.d { "Successfully loaded from modified path: $modifiedPath (${content.length} bytes)" }
                content
            }
        } catch (e: Exception) {
            // As a fallback, try without the prefix
            logger.w(e) { "Failed to load from modified path, trying original path: $path" }
            try {
                context.assets.open(path).bufferedReader().use {
                val content = it.readText()
                    logger.d { "Successfully loaded from original path: $path (${content.length} bytes)" }
                    content
                }
            } catch (e2: Exception) {
                logger.e(e2) { "Failed to load resource: $path" }
                throw IllegalArgumentException("Resource not found: $path", e2)
            }
        }
    }
}
