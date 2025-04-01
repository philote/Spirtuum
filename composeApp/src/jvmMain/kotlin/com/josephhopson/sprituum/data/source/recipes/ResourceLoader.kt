package com.josephhopson.sprituum.data.source.recipes

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStreamReader

/**
 * JVM implementation of resource loading
 */
actual suspend fun loadResourceAsString(path: String): String = withContext(Dispatchers.IO) {
    val classLoader = Thread.currentThread().contextClassLoader
        ?: JsonInitialRecipesProvider::class.java.classLoader
    val resource =
        classLoader.getResource(path) ?: throw IllegalArgumentException("Resource not found: $path")
    InputStreamReader(resource.openStream()).use { reader ->
        reader.readText()
    }
}
