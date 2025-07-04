package com.bignerdranch.android.fridgeiq

import androidx.lifecycle.LiveData
import androidx.room.*
import com.bignerdranch.android.fridgeiq.FoodItem
import java.util.Date

@Dao
interface FoodItemDao {
    @Query("SELECT * FROM food_items WHERE isConsumed = 0 ORDER BY expirationDate ASC")
    fun getAllActiveFoodItems(): LiveData<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE expirationDate <= :date AND isConsumed = 0")
    fun getExpiringItems(date: Date): LiveData<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE category = :category AND isConsumed = 0")
    fun getItemsByCategory(category: String): LiveData<List<FoodItem>>

    @Query("SELECT * FROM food_items WHERE storageLocation = :location AND isConsumed = 0")
    fun getItemsByLocation(location: String): LiveData<List<FoodItem>>

    @Insert
    suspend fun insertFoodItem(foodItem: FoodItem): Long

    @Update
    suspend fun updateFoodItem(foodItem: FoodItem)

    @Delete
    suspend fun deleteFoodItem(foodItem: FoodItem)

    @Query("SELECT DISTINCT category FROM food_items")
    fun getAllCategories(): LiveData<List<String>>

    @Query("SELECT DISTINCT storageLocation FROM food_items")
    fun getAllStorageLocations(): LiveData<List<String>>
}