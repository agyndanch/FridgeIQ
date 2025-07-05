package com.bignerdranch.android.fridgeiq.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import android.widget.CheckBox
import android.widget.Spinner
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.android.fridgeiq.R
import com.bignerdranch.android.fridgeiq.databinding.FragmentInventoryBinding
import com.bignerdranch.android.fridgeiq.ui.adapter.FoodItemAdapter
import com.bignerdranch.android.fridgeiq.ui.viewmodel.FoodItemViewModel
import com.bignerdranch.android.fridgeiq.data.entity.FoodItem
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class InventoryFragment : Fragment() {

    private var _binding: FragmentInventoryBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FoodItemViewModel by viewModels()
    private lateinit var adapter: FoodItemAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentInventoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setupRecyclerView()
        observeViewModel()
        setupFab()
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.inventory_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_scan_barcode -> {
                        findNavController().navigate(R.id.action_inventory_to_barcode_scanner)
                        true
                    }
                    R.id.action_filter -> {
                        showFilterDialog()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupRecyclerView() {
        adapter = FoodItemAdapter(
            onItemClick = { foodItem ->
                val action = InventoryFragmentDirections.actionInventoryToAddEditFood(foodItem)
                findNavController().navigate(action)
            },
            onMarkConsumed = { foodItem ->
                viewModel.markAsConsumed(foodItem)
            },
            onMarkWasted = { foodItem ->
                showWasteReasonDialog(foodItem)
            }
        )

        binding.recyclerViewInventory.adapter = adapter
        binding.recyclerViewInventory.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allActiveFoodItems.collect { items ->
                    adapter.submitList(items)
                    binding.textEmptyState.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun setupFab() {
        binding.fabAddFood.setOnClickListener {
            findNavController().navigate(R.id.action_inventory_to_add_edit_food)
        }
    }

    private fun showWasteReasonDialog(foodItem: FoodItem) {
        val reasons = arrayOf("Expired", "Spoiled", "Forgot about it", "Too much", "Other")

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Why was this item wasted?")
            .setItems(reasons) { _, which ->
                viewModel.markAsWasted(foodItem, reasons[which])
            }
            .show()
    }

    private fun showFilterDialog() {
        val categories = arrayOf("All Categories", "Dairy", "Meat", "Vegetables", "Fruits", "Pantry", "Frozen", "Beverages", "Other")
        val locations = arrayOf("All Locations", "Refrigerator", "Freezer", "Pantry", "Counter", "Other")

        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_filter, null)
        val categorySpinner = dialogView.findViewById<Spinner>(R.id.spinner_filter_category)
        val locationSpinner = dialogView.findViewById<Spinner>(R.id.spinner_filter_location)
        val expiringOnlyCheckbox = dialogView.findViewById<CheckBox>(R.id.checkbox_expiring_only)

        // Setup spinners
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        categorySpinner.adapter = categoryAdapter

        val locationAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, locations)
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        locationSpinner.adapter = locationAdapter

        MaterialAlertDialogBuilder(requireContext())
            .setTitle("Filter Items")
            .setView(dialogView)
            .setPositiveButton("Apply") { _, _ ->
                val selectedCategory = if (categorySpinner.selectedItemPosition == 0) null
                else categories[categorySpinner.selectedItemPosition]
                val selectedLocation = if (locationSpinner.selectedItemPosition == 0) null
                else locations[locationSpinner.selectedItemPosition]
                val expiringOnly = expiringOnlyCheckbox.isChecked

                viewModel.applyFilters(selectedCategory, selectedLocation, expiringOnly)
            }
            .setNegativeButton("Clear") { _, _ ->
                viewModel.clearFilters()
            }
            .setNeutralButton("Cancel", null)
            .show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}