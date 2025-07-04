package com.bignerdranch.android.fridgeiq.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bignerdranch.android.fridgeiq.data.entity.WasteEntry
import java.util.Date

@Dao
interface WasteEntryDao {
    @Query("SELECT * FROM waste_entries ORDER BY wasteDate DESC")
    fun getAllWasteEntries(): LiveData<List<WasteEntry>>

    @Query("SELECT SUM(estimatedCost) FROM waste_entries WHERE wasteDate >= :startDate")
    suspend fun getTotalWasteCostSince(startDate: Date): Double?

    @Query("SELECT COUNT(*) FROM waste_entries WHERE wasteDate >= :startDate")
    suspend fun getWasteItemCountSince(startDate: Date): Int

    @Insert
    suspend fun insertWasteEntry(wasteEntry: WasteEntry)
}