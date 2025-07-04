package com.bignerdranch.android.fridgeiq.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.bignerdranch.android.fridgeiq.data.database.FridgeIQDatabase
import com.bignerdranch.android.fridgeiq.data.entity.WasteEntry
import com.bignerdranch.android.fridgeiq.data.repository.FridgeIQRepository
import java.util.Calendar

class AnalyticsViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: FridgeIQRepository
    val allWasteEntries: LiveData<List<WasteEntry>>

    init {
        val database = FridgeIQDatabase.getDatabase(application)
        repository = FridgeIQRepository(
            database.foodItemDao(),
            database.wasteEntryDao(),
            database.shoppingListDao()
        )
        allWasteEntries = repository.getAllWasteEntries()
    }

    fun getMonthlyWasteCost(): LiveData<Double> = liveData {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.MONTH, -1)
        val cost = repository.getTotalWasteCostSince(calendar.time)
        emit(cost)
    }

    fun getWeeklyWasteCount(): LiveData<Int> = liveData {
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.WEEK_OF_YEAR, -1)
        val count = repository.getWasteItemCountSince(calendar.time)
        emit(count)
    }
}