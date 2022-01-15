package com.example.synonyms.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase

@Database(entities = [Synonyms::class, Results::class], version = 1, exportSchema = false)
abstract class AppDatabase: RoomDatabase() {

    abstract fun synonymsDao(): SynonymsDao
    abstract fun resultsDao(): ResultsDao

    companion object {
        // Singleton prevents multiple instances of database opening at the same time.
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            // if the INSTANCE is not null, then return it, if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "synonyms_database")
                    .createFromAsset("database/synonyms.db")
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}