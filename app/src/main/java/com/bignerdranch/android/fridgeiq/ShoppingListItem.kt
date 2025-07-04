package com.bignerdranch.android.fridgeiq

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "shopping_list")
data class ShoppingListItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: String,
    val quantity: Int = 1,
    val unit: String = "piece",
    val isPurchased: Boolean = false,
    val isGenerated: Boolean = false,
    val estimatedCost: Double = 0.0,
    val notes: String? = null
)