package com.bignerdranch.android.fridgeiq.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.bignerdranch.android.fridgeiq.data.database.FridgeIQDatabase
import com.bignerdranch.android.fridgeiq.data.entity.ShoppingListItem
import com.bignerdranch.android.fridgeiq.data.repository.FridgeIQRepository
import kotlinx.coroutines.launch

class ShoppingListViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FridgeIQRepository
    val allShoppingItems: LiveData<List<ShoppingListItem>>

    init {
        val database = FridgeIQDatabase.getDatabase(application)
        repository = FridgeIQRepository(
            database.foodItemDao(),
            database.wasteEntryDao(),
            database.shoppingListDao()
        )
        allShoppingItems = repository.getAllShoppingItems()
    }

    fun insertShoppingItem(item: ShoppingListItem) = viewModelScope.launch {
        repository.insertShoppingItem(item)
    }

    fun updateShoppingItem(item: ShoppingListItem) = viewModelScope.launch {
        repository.updateShoppingItem(item)
    }

    fun deleteShoppingItem(item: ShoppingListItem) = viewModelScope.launch {
        repository.deleteShoppingItem(item)
    }

    fun clearPurchasedItems() = viewModelScope.launch {
        repository.clearPurchasedItems()
    }
}