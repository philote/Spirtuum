package com.josephhopson.sprituum.data.source.local

import android.content.Context
import androidx.room.Room

/**
 * Android-specific implementation of DatabaseProvider
 */
class AndroidDatabaseProvider(private val context: Context) : DatabaseProvider {

    override fun provideDatabase(): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "spirituum-db"
        ).build()
    }
}
