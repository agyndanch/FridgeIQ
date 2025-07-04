package com.bignerdranch.android.fridgeiq.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date

@Entity(tableName = "food_items")
@Parcelize
data class FoodItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val barcode: String? = null,
    val category: String,
    val purchaseDate: Date,
    val expirationDate: Date,
    val storageLocation: String,
    val quantity: Int = 1,
    val unit: String = "piece",
    val isConsumed: Boolean = false,
    val isExpired: Boolean = false,
    val imagePath: String? = null,
    val estimatedCost: Double = 0.0,
    val notes: String? = null
) : Parcelable