package com.bignerdranch.android.fridgeiq.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.bignerdranch.android.fridgeiq.data.entity.WasteEntry
import com.bignerdranch.android.fridgeiq.data.repository.FridgeIQRepository
import java.util.Calendar

class AnalyticsViewModel : ViewModel() {

    private val repository = FridgeIQRepository.get()

    private val _allWasteEntries = MutableStateFlow<List<WasteEntry>>(emptyList())
    val allWasteEntries: StateFlow<List<WasteEntry>> = _allWasteEntries.asStateFlow()

    private val _monthlyWasteCost = MutableStateFlow(0.0)
    val monthlyWasteCost: StateFlow<Double> = _monthlyWasteCost.asStateFlow()

    private val _weeklyWasteCount = MutableStateFlow(0)
    val weeklyWasteCount: StateFlow<Int> = _weeklyWasteCount.asStateFlow()

    init {
        viewModelScope.launch {
            repository.getAllWasteEntries().collect {
                _allWasteEntries.value = it
            }
        }

        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.MONTH, -1)
            val cost = repository.getTotalWasteCostSince(calendar.time)
            _monthlyWasteCost.value = cost
        }

        viewModelScope.launch {
            val calendar = Calendar.getInstance()
            calendar.add(Calendar.WEEK_OF_YEAR, -1)
            val count = repository.getWasteItemCountSince(calendar.time)
            _weeklyWasteCount.value = count
        }
    }
}