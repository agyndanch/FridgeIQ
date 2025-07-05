package com.bignerdranch.android.fridgeiq.data.repository

import kotlinx.coroutines.flow.Flow
import com.bignerdranch.android.fridgeiq.data.dao.FoodItemDao
import com.bignerdranch.android.fridgeiq.data.dao.ShoppingListDao
import com.bignerdranch.android.fridgeiq.data.dao.WasteEntryDao
import com.bignerdranch.android.fridgeiq.data.entity.FoodItem
import com.bignerdranch.android.fridgeiq.data.entity.ShoppingListItem
import com.bignerdranch.android.fridgeiq.data.entity.WasteEntry
import java.util.Date

class FridgeIQRepository private constructor(
    private val foodItemDao: FoodItemDao,
    private val wasteEntryDao: WasteEntryDao,
    private val shoppingListDao: ShoppingListDao
) {

    // Food Items
    fun getAllActiveFoodItems(): Flow<List<FoodItem>> = foodItemDao.getAllActiveFoodItems()

    fun getExpiringItems(date: Date): Flow<List<FoodItem>> = foodItemDao.getExpiringItems(date)

    suspend fun insertFoodItem(foodItem: FoodItem): Long = foodItemDao.insertFoodItem(foodItem)

    suspend fun updateFoodItem(foodItem: FoodItem) = foodItemDao.updateFoodItem(foodItem)

    fun getAllCategories(): Flow<List<String>> = foodItemDao.getAllCategories()

    // Waste Entries
    fun getAllWasteEntries(): Flow<List<WasteEntry>> = wasteEntryDao.getAllWasteEntries()

    suspend fun insertWasteEntry(wasteEntry: WasteEntry) = wasteEntryDao.insertWasteEntry(wasteEntry)

    suspend fun getTotalWasteCostSince(startDate: Date): Double = wasteEntryDao.getTotalWasteCostSince(startDate) ?: 0.0

    suspend fun getWasteItemCountSince(startDate: Date): Int = wasteEntryDao.getWasteItemCountSince(startDate)

    // Shopping List
    fun getAllShoppingItems(): Flow<List<ShoppingListItem>> = shoppingListDao.getAllShoppingItems()

    suspend fun insertShoppingItem(item: ShoppingListItem) = shoppingListDao.insertShoppingItem(item)

    suspend fun updateShoppingItem(item: ShoppingListItem) = shoppingListDao.updateShoppingItem(item)

    suspend fun clearPurchasedItems() = shoppingListDao.clearPurchasedItems()

    companion object {
        @Volatile
        private var INSTANCE: FridgeIQRepository? = null

        fun initialize(
            foodItemDao: FoodItemDao,
            wasteEntryDao: WasteEntryDao,
            shoppingListDao: ShoppingListDao
        ) {
            if (INSTANCE == null) {
                INSTANCE = FridgeIQRepository(foodItemDao, wasteEntryDao, shoppingListDao)
            }
        }

        fun get(): FridgeIQRepository {
            return INSTANCE ?: throw IllegalStateException("FridgeIQRepository must be initialized")
        }
    }
}