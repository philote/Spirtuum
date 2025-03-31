package com.josephhopson.sprituum.ui.test

import android.graphics.Bitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.test.SemanticsNodeInteraction
import androidx.compose.ui.test.captureToImage
import androidx.test.platform.app.InstrumentationRegistry
import java.io.FileOutputStream
import kotlin.math.abs

/**
 * Utility functions for testing Compose UI components
 */
object ComposeTestUtils {

    /**
     * Compare a node's current appearance with a reference screenshot
     *
     * @param node The semantic node to capture
     * @param referenceScreenshotName The name of the reference screenshot file
     * @return True if the images match or if in record mode
     */
    fun compareWithReferenceScreenshot(
        node: SemanticsNodeInteraction,
        referenceScreenshotName: String,
        recordMode: Boolean = false,
        bitmapMatcher: BitmapMatcher = DefaultBitmapMatcher(0.99f)
    ): Boolean {
        // Capture the current appearance of the node
        val bitmap = node.captureToImage().asAndroidBitmap()

        // In record mode, save the screenshot as reference
        if (recordMode) {
            saveScreenshot(bitmap, referenceScreenshotName)
            return true
        }

        // Compare with reference screenshot if it exists
        val referenceScreenshot = loadReferenceScreenshot(referenceScreenshotName)
        return if (referenceScreenshot != null) {
            bitmapMatcher.matches(bitmap, referenceScreenshot)
        } else {
            // If no reference exists, save the current screenshot
            saveScreenshot(bitmap, referenceScreenshotName)
            true
        }
    }

    /**
     * Save a screenshot to the device's storage
     *
     * @param bitmap The bitmap to save
     * @param fileName The name of the file to save
     */
    private fun saveScreenshot(bitmap: Bitmap, fileName: String) {
        val context = InstrumentationRegistry.getInstrumentation().context
        val filesDir = context.filesDir
        val screenshotFile = filesDir.resolve("$fileName.png")

        FileOutputStream(screenshotFile).use { out ->
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
        }
    }

    /**
     * Load a reference screenshot from the device's storage
     *
     * @param fileName The name of the reference screenshot file
     * @return The reference bitmap, or null if it doesn't exist
     */
    private fun loadReferenceScreenshot(fileName: String): Bitmap? {
        val context = InstrumentationRegistry.getInstrumentation().context
        val filesDir = context.filesDir
        val referenceFile = filesDir.resolve("$fileName.png")

        return if (referenceFile.exists()) {
            val options = android.graphics.BitmapFactory.Options()
            android.graphics.BitmapFactory.decodeFile(referenceFile.absolutePath, options)
        } else {
            null
        }
    }

    /**
     * Interface for bitmap comparison
     */
    interface BitmapMatcher {
        /**
         * Check if two bitmaps match
         *
         * @param actual The actual bitmap
         * @param expected The expected bitmap
         * @return True if the bitmaps match
         */
        fun matches(actual: Bitmap, expected: Bitmap): Boolean
    }

    /**
     * Default bitmap matcher that compares pixels with a similarity threshold
     *
     * @param similarityThreshold The threshold for similarity (0.0 - 1.0)
     */
    class DefaultBitmapMatcher(private val similarityThreshold: Float) : BitmapMatcher {

        override fun matches(actual: Bitmap, expected: Bitmap): Boolean {
            // Check dimensions match
            if (actual.width != expected.width || actual.height != expected.height) {
                return false
            }

            // Count matching pixels
            val width = actual.width
            val height = actual.height
            var matchingPixels = 0

            for (x in 0 until width) {
                for (y in 0 until height) {
                    if (pixelsMatch(actual.getPixel(x, y), expected.getPixel(x, y))) {
                        matchingPixels++
                    }
                }
            }

            // Calculate similarity
            val totalPixels = width * height
            val similarity = matchingPixels.toFloat() / totalPixels

            return similarity >= similarityThreshold
        }

        /**
         * Check if two pixels match with some tolerance
         */
        private fun pixelsMatch(pixel1: Int, pixel2: Int): Boolean {
            val r1 = pixel1 shr 16 and 0xff
            val g1 = pixel1 shr 8 and 0xff
            val b1 = pixel1 and 0xff

            val r2 = pixel2 shr 16 and 0xff
            val g2 = pixel2 shr 8 and 0xff
            val b2 = pixel2 and 0xff

            // Allow some small difference in each channel
            val tolerance = 5
            return abs(r1 - r2) <= tolerance &&
                    abs(g1 - g2) <= tolerance &&
                    abs(b1 - b2) <= tolerance
        }
    }
}