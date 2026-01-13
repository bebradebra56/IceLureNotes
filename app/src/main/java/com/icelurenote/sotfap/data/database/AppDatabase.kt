package com.icelurenote.sotfap.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.icelurenote.sotfap.data.dao.FishingEntryDao
import com.icelurenote.sotfap.data.model.Converters
import com.icelurenote.sotfap.data.model.FishingEntry

@Database(
    entities = [FishingEntry::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    
    abstract fun fishingEntryDao(): FishingEntryDao
    
    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "ice_lure_notes_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

