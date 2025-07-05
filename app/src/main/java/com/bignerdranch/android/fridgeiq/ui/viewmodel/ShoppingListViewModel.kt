package com.bignerdranch.android.fridgeiq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.bignerdranch.android.fridgeiq.data.entity.ShoppingListItem
import com.bignerdranch.android.fridgeiq.data.repository.FridgeIQRepository

class ShoppingListViewModel : ViewModel() {

    private val repository = FridgeIQRepository.get()

    private val _allShoppingItems = MutableStateFlow<List<ShoppingListItem>>(emptyList())
    val allShoppingItems: StateFlow<List<ShoppingListItem>> = _allShoppingItems.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllShoppingItems().collect {
                _allShoppingItems.value = it
            }
        }
    }

    fun insertShoppingItem(item: ShoppingListItem) = viewModelScope.launch {
        repository.insertShoppingItem(item)
    }

    fun updateShoppingItem(item: ShoppingListItem) = viewModelScope.launch {
        repository.updateShoppingItem(item)
    }

    fun clearPurchasedItems() = viewModelScope.launch {
        repository.clearPurchasedItems()
    }
}