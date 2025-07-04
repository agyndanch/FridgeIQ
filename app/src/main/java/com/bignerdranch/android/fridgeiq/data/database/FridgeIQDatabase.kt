package com.bignerdranch.android.fridgeiq.data.database

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.bignerdranch.android.fridgeiq.data.dao.FoodItemDao
import com.bignerdranch.android.fridgeiq.data.dao.ShoppingListDao
import com.bignerdranch.android.fridgeiq.data.dao.WasteEntryDao
import com.bignerdranch.android.fridgeiq.data.converter.Converters
import com.bignerdranch.android.fridgeiq.data.entity.FoodItem
import com.bignerdranch.android.fridgeiq.data.entity.ShoppingListItem
import com.bignerdranch.android.fridgeiq.data.entity.WasteEntry

@Database(
    entities = [FoodItem::class, WasteEntry::class, ShoppingListItem::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class FridgeIQDatabase : RoomDatabase() {

    abstract fun foodItemDao(): FoodItemDao
    abstract fun wasteEntryDao(): WasteEntryDao
    abstract fun shoppingListDao(): ShoppingListDao

    companion object {
        @Volatile
        private var INSTANCE: FridgeIQDatabase? = null

        fun getDatabase(context: Context): FridgeIQDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    FridgeIQDatabase::class.java,
                    "fridgeiq_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}