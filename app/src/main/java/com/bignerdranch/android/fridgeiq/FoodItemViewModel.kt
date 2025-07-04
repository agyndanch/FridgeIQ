package com.bignerdranch.android.fridgeiq

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.fridgeiq.FridgeIQDatabase
import com.bignerdranch.android.fridgeiq.FoodItem
import com.bignerdranch.android.fridgeiq.WasteEntry
import com.bignerdranch.android.fridgeiq.FridgeIQRepository
import kotlinx.coroutines.launch
import java.util.Date
import java.util.Calendar

class FoodItemViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FridgeIQRepository
    val allActiveFoodItems: LiveData<List<FoodItem>>
    val expiringItems: LiveData<List<FoodItem>>
    val allCategories: LiveData<List<String>>

    init {
        val database = FridgeIQDatabase.getDatabase(application)
        repository = FridgeIQRepository(
            database.foodItemDao(),
            database.wasteEntryDao(),
            database.shoppingListDao()
        )
        allActiveFoodItems = repository.getAllActiveFoodItems()

        // Get items expiring in the next 3 days
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 3)
        expiringItems = repository.getExpiringItems(calendar.time)

        allCategories = repository.getAllCategories()
    }

    fun insertFoodItem(foodItem: FoodItem) = viewModelScope.launch {
        repository.insertFoodItem(foodItem)
    }

    fun updateFoodItem(foodItem: FoodItem) = viewModelScope.launch {
        repository.updateFoodItem(foodItem)
    }

    fun deleteFoodItem(foodItem: FoodItem) = viewModelScope.launch {
        repository.deleteFoodItem(foodItem)
    }

    fun markAsConsumed(foodItem: FoodItem) = viewModelScope.launch {
        repository.updateFoodItem(foodItem.copy(isConsumed = true))
    }

    fun markAsWasted(foodItem: FoodItem, reason: String) = viewModelScope.launch {
        // Mark item as consumed
        repository.updateFoodItem(foodItem.copy(isConsumed = true, isExpired = true))

        // Create waste entry
        val wasteEntry = WasteEntry(
            foodItemId = foodItem.id,
            foodName = foodItem.name,
            category = foodItem.category,
            wasteDate = Date(),
            wasteReason = reason,
            estimatedCost = foodItem.estimatedCost,
            quantity = foodItem.quantity,
            unit = foodItem.unit
        )
        repository.insertWasteEntry(wasteEntry)
    }

    fun getItemsByCategory(category: String): LiveData<List<FoodItem>> {
        return repository.getItemsByCategory(category)
    }
}