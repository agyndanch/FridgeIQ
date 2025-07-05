package com.bignerdranch.android.fridgeiq.ui.fragment

import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bignerdranch.android.fridgeiq.R
import com.bignerdranch.android.fridgeiq.data.entity.ShoppingListItem
import com.bignerdranch.android.fridgeiq.databinding.FragmentAddEditShoppingItemBinding
import com.bignerdranch.android.fridgeiq.ui.viewmodel.ShoppingListViewModel

class AddEditShoppingItemFragment : Fragment() {

    private var _binding: FragmentAddEditShoppingItemBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShoppingListViewModel by viewModels()
    private val args: AddEditShoppingItemFragmentArgs by navArgs()

    private lateinit var categoryAdapter: ArrayAdapter<String>
    private lateinit var unitAdapter: ArrayAdapter<String>

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditShoppingItemBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupMenu()
        setupSpinners()
        populateFields()
    }

    private fun setupMenu() {
        requireActivity().addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.add_edit_food_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_save -> {
                        saveShoppingItem()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupSpinners() {
        // Categories
        val categories = arrayOf("Dairy", "Meat", "Vegetables", "Fruits", "Pantry", "Frozen", "Beverages", "Other")
        categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = categoryAdapter

        // Units
        val units = arrayOf("piece", "lb", "oz", "kg", "g", "cup", "tbsp", "tsp", "bottle", "can", "box", "bag")
        unitAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, units)
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerUnit.adapter = unitAdapter
    }

    private fun populateFields() {
        args.shoppingItem?.let { item ->
            binding.apply {
                editTextName.setText(item.name)
                editTextQuantity.setText(item.run { quantity.toString() })
                editTextCost.setText(item.run { estimatedCost.toString() })
                editTextNotes.setText(item.notes)

                // Set spinner selections
                val categoryPos = categoryAdapter.getPosition(item.category)
                if (categoryPos >= 0) spinnerCategory.setSelection(categoryPos)

                val unitPos = unitAdapter.getPosition(item.unit)
                if (unitPos >= 0) spinnerUnit.setSelection(unitPos)
            }
        }
    }

    private fun saveShoppingItem() {
        val name = binding.editTextName.text.toString().trim()
        val quantityStr = binding.editTextQuantity.text.toString().trim()
        val costStr = binding.editTextCost.text.toString().trim()

        if (name.isEmpty()) {
            binding.editTextName.error = "Name is required"
            return
        }

        val quantity = quantityStr.toIntOrNull() ?: 1
        val cost = costStr.toDoubleOrNull() ?: 0.0

        val shoppingItem = args.shoppingItem?.copy(
            name = name,
            category = binding.spinnerCategory.selectedItem.toString(),
            quantity = quantity,
            unit = binding.spinnerUnit.selectedItem.toString(),
            estimatedCost = cost,
            notes = binding.editTextNotes.text.toString().trim()
        ) ?: ShoppingListItem(
            name = name,
            category = binding.spinnerCategory.selectedItem.toString(),
            quantity = quantity,
            unit = binding.spinnerUnit.selectedItem.toString(),
            estimatedCost = cost,
            notes = binding.editTextNotes.text.toString().trim()
        )

        if (args.shoppingItem != null) {
            viewModel.updateShoppingItem(shoppingItem)
        } else {
            viewModel.insertShoppingItem(shoppingItem)
        }

        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}