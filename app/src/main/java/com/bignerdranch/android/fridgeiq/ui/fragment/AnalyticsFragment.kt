package com.bignerdranch.android.fridgeiq.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.bignerdranch.android.fridgeiq.databinding.FragmentAnalyticsBinding
import com.bignerdranch.android.fridgeiq.ui.viewmodel.AnalyticsViewModel
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.utils.ColorTemplate

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
        // Configure pie chart for waste categories
        binding.chartWasteByCategory.apply {
            description.isEnabled = false
            legend.isEnabled = true
            setDrawHoleEnabled(true)
            setHoleColor(android.R.color.transparent)
            setTransparentCircleColor(android.R.color.transparent)
            setDrawCenterText(true)
            centerText = "Waste by Category"
        }
    }

    private fun observeViewModel() {
        viewModel.getMonthlyWasteCost().observe(viewLifecycleOwner) { cost ->
            binding.textMonthlyCost.text = "$%.2f".format(cost)
        }

        viewModel.getWeeklyWasteCount().observe(viewLifecycleOwner) { count ->
            binding.textWeeklyCount.text = count.toString()
        }

        viewModel.allWasteEntries.observe(viewLifecycleOwner) { entries ->
            updateWasteChart(entries)
        }
    }

    private fun updateWasteChart(entries: List<com.example.fridgeiq.data.entity.WasteEntry>) {
        val categoryMap = entries.groupBy { it.category }
            .mapValues { (_, items) -> items.sumOf { it.estimatedCost } }

        val pieEntries = categoryMap.map { (category, cost) ->
            PieEntry(cost.toFloat(), category)
        }

        val dataSet = PieDataSet(pieEntries, "Waste by Category")
        dataSet.colors = ColorTemplate.MATERIAL_COLORS.toList()

        val pieData = PieData(dataSet)
        binding.chartWasteByCategory.data = pieData
        binding.chartWasteByCategory.invalidate()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}