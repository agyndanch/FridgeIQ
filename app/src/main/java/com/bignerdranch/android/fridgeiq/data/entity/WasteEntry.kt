package com.bignerdranch.android.fridgeiq.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "waste_entries")
data class WasteEntry(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val foodItemId: Long,
    val foodName: String,
    val category: String,
    val wasteDate: Date,
    val wasteReason: String,
    val estimatedCost: Double,
    val quantity: Int,
    val unit: String
)