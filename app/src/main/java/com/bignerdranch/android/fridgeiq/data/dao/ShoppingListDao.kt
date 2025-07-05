package com.bignerdranch.android.fridgeiq.data.dao

import kotlinx.coroutines.flow.Flow
import androidx.room.*
import com.bignerdranch.android.fridgeiq.data.entity.ShoppingListItem

@Dao
interface ShoppingListDao {
    @Query("SELECT * FROM shopping_list ORDER BY isPurchased ASC, name ASC")
    fun getAllShoppingItems(): Flow<List<ShoppingListItem>>

    @Insert
    suspend fun insertShoppingItem(item: ShoppingListItem)

    @Update
    suspend fun updateShoppingItem(item: ShoppingListItem)

    @Delete
    suspend fun deleteShoppingItem(item: ShoppingListItem)

    @Query("DELETE FROM shopping_list WHERE isPurchased = 1")
    suspend fun clearPurchasedItems()
}