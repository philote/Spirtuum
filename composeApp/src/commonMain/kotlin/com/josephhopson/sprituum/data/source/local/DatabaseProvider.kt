package com.josephhopson.sprituum.data.source.local

/**
 * Common interface for database provider
 *
 * Each platform needs to provide its own implementation
 */
interface DatabaseProvider {
    /**
     * Create and return the AppDatabase instance
     */
    fun provideDatabase(): AppDatabase
}
