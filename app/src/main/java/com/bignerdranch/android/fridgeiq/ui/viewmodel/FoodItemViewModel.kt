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

    private val _filteredFoodItems = MutableStateFlow<List<FoodItem>>(emptyList())
    private var currentFilters = FilterCriteria()

    private val _displayedFoodItems = MutableStateFlow<List<FoodItem>>(emptyList())
    val displayedFoodItems: StateFlow<List<FoodItem>> = _displayedFoodItems.asStateFlow()
    private var hasActiveFilters = false

    init {
        viewModelScope.launch {
            repository.getAllActiveFoodItems().collect { items ->
                _allActiveFoodItems.value = items
                applyCurrentFilters()
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

    data class FilterCriteria(
        val category: String? = null,
        val location: String? = null,
        val expiringOnly: Boolean = false
    )

    fun applyFilters(category: String?, location: String?, expiringOnly: Boolean) {
        currentFilters = FilterCriteria(category, location, expiringOnly)
        hasActiveFilters = category != null || location != null || expiringOnly
        applyCurrentFilters()
    }

    fun clearFilters() {
        currentFilters = FilterCriteria()
        hasActiveFilters = false
        applyCurrentFilters()
    }

    private fun applyCurrentFilters() {
        val allItems = _allActiveFoodItems.value

        if (!hasActiveFilters) {
            _displayedFoodItems.value = allItems
            return
        }

        val filtered = allItems.filter { item ->
            val categoryMatch = currentFilters.category?.let {
                item.category == it
            } ?: true

            val locationMatch = currentFilters.location?.let {
                item.storageLocation == it
            } ?: true

            val expiringMatch = if (currentFilters.expiringOnly) {
                val threeDaysFromNow = Calendar.getInstance().apply {
                    add(Calendar.DAY_OF_YEAR, 3)
                }.time
                item.expirationDate.before(threeDaysFromNow) || item.expirationDate == threeDaysFromNow
            } else true

            categoryMatch && locationMatch && expiringMatch
        }

        _displayedFoodItems.value = filtered
    }
}