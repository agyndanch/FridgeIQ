package com.bignerdranch.android.fridgeiq.data.repository

import androidx.lifecycle.LiveData
import com.bignerdranch.android.fridgeiq.data.dao.FoodItemDao
import com.bignerdranch.android.fridgeiq.data.dao.ShoppingListDao
import com.bignerdranch.android.fridgeiq.data.dao.WasteEntryDao
import com.bignerdranch.android.fridgeiq.data.entity.FoodItem
import com.bignerdranch.android.fridgeiq.data.entity.ShoppingListItem
import com.bignerdranch.android.fridgeiq.data.entity.WasteEntry
import java.util.Date

class FridgeIQRepository(
    private val foodItemDao: FoodItemDao,
    private val wasteEntryDao: WasteEntryDao,
    private val shoppingListDao: ShoppingListDao
) {

    // Food Items
    fun getAllActiveFoodItems(): LiveData<List<FoodItem>> = foodItemDao.getAllActiveFoodItems()

    fun getExpiringItems(date: Date): LiveData<List<FoodItem>> = foodItemDao.getExpiringItems(date)

    fun getItemsByCategory(category: String): LiveData<List<FoodItem>> = foodItemDao.getItemsByCategory(category)

    suspend fun insertFoodItem(foodItem: FoodItem): Long = foodItemDao.insertFoodItem(foodItem)

    suspend fun updateFoodItem(foodItem: FoodItem) = foodItemDao.updateFoodItem(foodItem)

    suspend fun deleteFoodItem(foodItem: FoodItem) = foodItemDao.deleteFoodItem(foodItem)

    fun getAllCategories(): LiveData<List<String>> = foodItemDao.getAllCategories()

    // Waste Entries
    fun getAllWasteEntries(): LiveData<List<WasteEntry>> = wasteEntryDao.getAllWasteEntries()

    suspend fun insertWasteEntry(wasteEntry: WasteEntry) = wasteEntryDao.insertWasteEntry(wasteEntry)

    suspend fun getTotalWasteCostSince(startDate: Date): Double = wasteEntryDao.getTotalWasteCostSince(startDate) ?: 0.0

    suspend fun getWasteItemCountSince(startDate: Date): Int = wasteEntryDao.getWasteItemCountSince(startDate)

    // Shopping List
    fun getAllShoppingItems(): LiveData<List<ShoppingListItem>> = shoppingListDao.getAllShoppingItems()

    suspend fun insertShoppingItem(item: ShoppingListItem) = shoppingListDao.insertShoppingItem(item)

    suspend fun updateShoppingItem(item: ShoppingListItem) = shoppingListDao.updateShoppingItem(item)

    suspend fun deleteShoppingItem(item: ShoppingListItem) = shoppingListDao.deleteShoppingItem(item)

    suspend fun clearPurchasedItems() = shoppingListDao.clearPurchasedItems()
}