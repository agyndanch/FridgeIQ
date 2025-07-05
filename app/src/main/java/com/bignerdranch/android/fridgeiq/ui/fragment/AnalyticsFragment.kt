package com.bignerdranch.android.fridgeiq.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.bignerdranch.android.fridgeiq.data.entity.WasteEntry
import com.bignerdranch.android.fridgeiq.databinding.FragmentAnalyticsBinding
import com.bignerdranch.android.fridgeiq.ui.viewmodel.AnalyticsViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate
import kotlinx.coroutines.launch

class AnalyticsFragment : Fragment() {

    private var _binding: FragmentAnalyticsBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AnalyticsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAnalyticsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCharts()
        observeViewModel()
    }

    private fun setupCharts() {
        binding.chartWasteByCategory.apply {
            description.isEnabled = false
            legend.isEnabled = true
            isDrawHoleEnabled = true
            setHoleColor(android.R.color.transparent)
            setTransparentCircleColor(android.R.color.transparent)
            setDrawCenterText(true)
            centerText = "Waste by Category"
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.monthlyWasteCost.collect { cost ->
                    "$%.2f".format(cost).also { binding.textMonthlyCost.text = it }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.weeklyWasteCount.collect { count ->
                    count.toString().also { binding.textWeeklyCount.text = it }
                }
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allWasteEntries.collect { entries ->
                    updateWasteChart(entries)
                }
            }
        }
    }

    private fun updateWasteChart(entries: List<WasteEntry>) {
        val categoryMap = entries.groupBy { it.category }
            .mapValues { (_, items) -> items.sumOf { it.estimatedCost } }

        val pieEntries = categoryMap.map { (category, cost) ->
            PieEntry(cost.toFloat(), category)
        }

        if (pieEntries.isNotEmpty()) {
            val dataSet = PieDataSet(pieEntries, "").apply {
                colors = ColorTemplate.MATERIAL_COLORS.toList()
                valueTextSize = 16f
                valueTextColor = android.graphics.Color.BLACK

                sliceSpace = 2f
                selectionShift = 8f
            }

            val pieData = PieData(dataSet).apply {
                setValueTextSize(16f)
                setValueTextColor(android.graphics.Color.BLACK)
            }

            binding.chartWasteByCategory.data = pieData
        } else {
            binding.chartWasteByCategory.data = null
        }

        binding.chartWasteByCategory.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}