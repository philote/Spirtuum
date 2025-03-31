package com.josephhopson.sprituum.data.source.local

import androidx.room.Room
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import java.io.File
import kotlinx.coroutines.Dispatchers

/**
 * JVM implementation of the database provider for desktop platforms
 */
class JvmDatabaseProvider : DatabaseProvider {

    override fun provideDatabase(): AppDatabase {
        // Create app directory if it doesn't exist
        val userHome = System.getProperty("user.home")
        val appDir = File(userHome, ".spirituum")
        if (!appDir.exists()) {
            appDir.mkdirs()
        }

        // Create database file
        val dbFile = File(appDir, "spirituum.db")

        return Room.databaseBuilder<AppDatabase>(
            name = dbFile.absolutePath
        )
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
}