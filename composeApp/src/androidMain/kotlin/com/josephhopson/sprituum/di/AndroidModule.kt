package com.josephhopson.sprituum.di

import android.content.Context
import androidx.room.Room
import com.josephhopson.sprituum.data.source.local.AppDatabase
import org.koin.dsl.module

/**
 * Android-specific module for database initialization
 */
fun createAndroidModule(context: Context) = module {
    // Android-specific database initialization
    single {
        Room.databaseBuilder(
            context = context,
            klass = AppDatabase::class.java,
            name = "spirituum-db"
        ).build()
    }
}
