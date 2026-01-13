package com.icelurenote.sotfap.data.repository

import com.icelurenote.sotfap.data.dao.FishingEntryDao
import com.icelurenote.sotfap.data.model.FishingEntry
import kotlinx.coroutines.flow.Flow

class FishingRepository(private val fishingEntryDao: FishingEntryDao) {
    
    fun getAllEntries(): Flow<List<FishingEntry>> = fishingEntryDao.getAllEntries()
    
    suspend fun getEntryById(id: Long): FishingEntry? = fishingEntryDao.getEntryById(id)
    
    suspend fun insertEntry(entry: FishingEntry): Long = fishingEntryDao.insertEntry(entry)
    
    suspend fun updateEntry(entry: FishingEntry) = fishingEntryDao.updateEntry(entry)
    
    suspend fun deleteEntry(entry: FishingEntry) = fishingEntryDao.deleteEntry(entry)
    
    suspend fun deleteAllEntries() = fishingEntryDao.deleteAllEntries()
    
    fun getEntriesByDateRange(startTime: Long, endTime: Long): Flow<List<FishingEntry>> =
        fishingEntryDao.getEntriesByDateRange(startTime, endTime)
    
    fun getTotalEntriesCount(): Flow<Int> = fishingEntryDao.getTotalEntriesCount()
}

