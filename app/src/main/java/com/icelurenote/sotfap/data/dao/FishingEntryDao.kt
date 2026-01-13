package com.icelurenote.sotfap.data.dao

import androidx.room.*
import com.icelurenote.sotfap.data.model.FishingEntry
import kotlinx.coroutines.flow.Flow

@Dao
interface FishingEntryDao {
    
    @Query("SELECT * FROM fishing_entries ORDER BY timestamp DESC")
    fun getAllEntries(): Flow<List<FishingEntry>>
    
    @Query("SELECT * FROM fishing_entries WHERE id = :id")
    suspend fun getEntryById(id: Long): FishingEntry?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: FishingEntry): Long
    
    @Update
    suspend fun updateEntry(entry: FishingEntry)
    
    @Delete
    suspend fun deleteEntry(entry: FishingEntry)
    
    @Query("DELETE FROM fishing_entries")
    suspend fun deleteAllEntries()
    
    @Query("SELECT * FROM fishing_entries WHERE timestamp >= :startTime AND timestamp <= :endTime ORDER BY timestamp DESC")
    fun getEntriesByDateRange(startTime: Long, endTime: Long): Flow<List<FishingEntry>>
    
    @Query("SELECT COUNT(*) FROM fishing_entries")
    fun getTotalEntriesCount(): Flow<Int>
}

