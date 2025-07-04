package com.bignerdranch.android.fridgeiq.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.android.fridgeiq.R
import com.bignerdranch.android.fridgeiq.databinding.FragmentInventoryBinding
import com.bignerdranch.android.fridgeiq.ui.adapter.FoodItemAdapter
import com.bignerdranch.android.fridgeiq.ui.viewmodel.FoodItemViewModel
import com.bignerdranch.android.fridgeiq.data.entity.FoodItem
import com.google.android.material.dialog.MaterialAlertDialogBuilder

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
                        // Show filter dialog - placeholder for future implementation
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
        viewModel.allActiveFoodItems.observe(viewLifecycleOwner) { items ->
            adapter.submitList(items)
            binding.textEmptyState.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}