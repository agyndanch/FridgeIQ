package com.bignerdranch.android.fridgeiq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.bignerdranch.android.fridgeiq.data.entity.FoodItem
import com.bignerdranch.android.fridgeiq.data.entity.WasteEntry
import com.bignerdranch.android.fridgeiq.data.repository.FridgeIQRepository
import java.util.Date
import java.util.Calendar

class FoodItemViewModel : ViewModel() {

    private val repository = FridgeIQRepository.get()

    private val _allActiveFoodItems = MutableStateFlow<List<FoodItem>>(emptyList())
    val allActiveFoodItems: StateFlow<List<FoodItem>> = _allActiveFoodItems.asStateFlow()

    private val _expiringItems = MutableStateFlow<List<FoodItem>>(emptyList())

    private val _allCategories = MutableStateFlow<List<String>>(emptyList())

    init {
        viewModelScope.launch {
            repository.getAllActiveFoodItems().collect {
                _allActiveFoodItems.value = it
            }
        }

        viewModelScope.launch {
            // Get items expiring in the next 3 days
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.DAY_OF_YEAR, 3)
            repository.getExpiringItems(calendar.time).collect {
                _expiringItems.value = it
            }
        }

        viewModelScope.launch {
            repository.getAllCategories().collect {
                _allCategories.value = it
            }
        }
    }

    fun insertFoodItem(foodItem: FoodItem) = viewModelScope.launch {
        repository.insertFoodItem(foodItem)
    }

    fun updateFoodItem(foodItem: FoodItem) = viewModelScope.launch {
        repository.updateFoodItem(foodItem)
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
}