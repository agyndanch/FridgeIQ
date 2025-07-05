package com.bignerdranch.android.fridgeiq.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Entity(tableName = "shopping_list")
@Parcelize
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
) : Parcelable