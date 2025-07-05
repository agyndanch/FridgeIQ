package com.bignerdranch.android.fridgeiq.data.dao

import kotlinx.coroutines.flow.Flow
import androidx.room.*
import com.bignerdranch.android.fridgeiq.data.entity.FoodItem
import java.util.Date

@Dao
interface FoodItemDao {
    @Query("SELECT * FROM food_items WHERE isConsumed = 0 ORDER BY expirationDate ASC")
    fun getAllActiveFoodItems(): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE expirationDate <= :date AND isConsumed = 0")
    fun getExpiringItems(date: Date): Flow<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE category = :category AND isConsumed = 0")
    fun getItemsByCategory(category: String): Flow<List<FoodItem>>

    @Insert
    suspend fun insertFoodItem(foodItem: FoodItem): Long

    @Update
    suspend fun updateFoodItem(foodItem: FoodItem)

    @Delete
    suspend fun deleteFoodItem(foodItem: FoodItem)

    @Query("SELECT DISTINCT category FROM food_items")
    fun getAllCategories(): Flow<List<String>>
}