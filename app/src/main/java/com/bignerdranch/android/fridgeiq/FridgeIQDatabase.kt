package com.bignerdranch.android.fridgeiq

import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import android.content.Context
import com.bignerdranch.android.fridgeiq.Converters
import com.bignerdranch.android.fridgeiq.* // DAO
import com.bignerdranch.android.fridgeiq.* // ENTITY

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