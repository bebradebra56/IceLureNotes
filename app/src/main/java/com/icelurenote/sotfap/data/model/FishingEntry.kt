package com.icelurenote.sotfap.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import androidx.room.TypeConverters

@Entity(tableName = "fishing_entries")
@TypeConverters(Converters::class)
data class FishingEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val baitType: BaitType,
    val baitColor: BaitColor,
    val targetFish: FishType,
    val depth: Float, // in meters
    val result: Result,
    val notes: String = "",
    val timestamp: Long = System.currentTimeMillis()
)

class Converters {
    @TypeConverter
    fun fromBaitType(value: BaitType): String = value.name

    @TypeConverter
    fun toBaitType(value: String): BaitType = BaitType.valueOf(value)

    @TypeConverter
    fun fromBaitColor(value: BaitColor): String = value.name

    @TypeConverter
    fun toBaitColor(value: String): BaitColor = BaitColor.valueOf(value)

    @TypeConverter
    fun fromFishType(value: FishType): String = value.name

    @TypeConverter
    fun toFishType(value: String): FishType = FishType.valueOf(value)

    @TypeConverter
    fun fromResult(value: Result): String = value.name

    @TypeConverter
    fun toResult(value: String): Result = Result.valueOf(value)
}

