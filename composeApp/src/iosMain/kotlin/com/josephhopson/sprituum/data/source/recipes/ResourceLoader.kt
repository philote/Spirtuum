@file:OptIn(kotlinx.cinterop.ExperimentalForeignApi::class)

package com.josephhopson.sprituum.data.source.recipes

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import platform.Foundation.NSBundle
import platform.Foundation.NSString
import platform.Foundation.NSUTF8StringEncoding
import platform.Foundation.stringWithContentsOfFile

/**
 * iOS implementation of resource loading
 */
actual suspend fun loadResourceAsString(path: String): String = withContext(Dispatchers.Default) {
    // Adjust path to get the file name and extension
    val parts = path.split("/").last().split(".")
    val fileName =
        parts.firstOrNull() ?: throw IllegalArgumentException("Invalid path format: $path")
    val extension = parts.getOrNull(1)
        ?: throw IllegalArgumentException("Missing file extension in path: $path")

    // Get the file path from the bundle
    val filePath = NSBundle.mainBundle.pathForResource(fileName, extension)
        ?: throw IllegalArgumentException("Resource not found: $path")

    // Read the file contents
    NSString.stringWithContentsOfFile(filePath, NSUTF8StringEncoding, null)
        ?: throw IllegalArgumentException("Failed to read resource: $path")
}
