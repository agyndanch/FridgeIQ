package com.bignerdranch.android.fridgeiq.ui.fragment

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.*
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bignerdranch.android.fridgeiq.R
import com.bignerdranch.android.fridgeiq.data.entity.FoodItem
import com.bignerdranch.android.fridgeiq.databinding.FragmentAddEditFoodBinding
import com.bignerdranch.android.fridgeiq.ui.viewmodel.FoodItemViewModel
import java.text.SimpleDateFormat
import java.util.*

class AddEditFoodFragment : Fragment() {

    private var _binding: FragmentAddEditFoodBinding? = null
    private val binding get() = _binding!!

    private val viewModel: FoodItemViewModel by viewModels()
    private val args: AddEditFoodFragmentArgs by navArgs()

    private var selectedExpirationDate = Date()
    private val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddEditFoodBinding.inflate(inflater, container, false)
        setHasOptionsMenu(true)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupSpinners()
        setupDatePicker()
        populateFields()

        // Set default expiration date to 7 days from now
        val calendar = Calendar.getInstance()
        calendar.add(Calendar.DAY_OF_YEAR, 7)
        selectedExpirationDate = calendar.time
        binding.textExpirationDate.text = dateFormat.format(selectedExpirationDate)
    }

    private fun setupSpinners() {
        // Categories
        val categories = arrayOf("Dairy", "Meat", "Vegetables", "Fruits", "Pantry", "Frozen", "Beverages", "Other")
        val categoryAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = categoryAdapter

        // Storage locations
        val locations = arrayOf("Refrigerator", "Freezer", "Pantry", "Counter", "Other")
        val locationAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, locations)
        locationAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerStorageLocation.adapter = locationAdapter

        // Units
        val units = arrayOf("piece", "lb", "oz", "kg", "g", "cup", "tbsp", "tsp", "bottle", "can", "box", "bag")
        val unitAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, units)
        unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerUnit.adapter = unitAdapter
    }

    private fun setupDatePicker() {
        binding.textExpirationDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            calendar.time = selectedExpirationDate

            DatePickerDialog(
                requireContext(),
                { _, year, month, dayOfMonth ->
                    calendar.set(year, month, dayOfMonth)
                    selectedExpirationDate = calendar.time
                    binding.textExpirationDate.text = dateFormat.format(selectedExpirationDate)
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun populateFields() {
        args.foodItem?.let { foodItem ->
            binding.apply {
                editTextName.setText(foodItem.name)
                editTextQuantity.setText(foodItem.quantity.toString())
                editTextCost.setText(foodItem.estimatedCost.toString())
                editTextNotes.setText(foodItem.notes)

                // Set spinner selections
                val categoryPos = (spinnerCategory.adapter as ArrayAdapter<String>).getPosition(foodItem.category)
                spinnerCategory.setSelection(categoryPos)

                val locationPos = (spinnerStorageLocation.adapter as ArrayAdapter<String>).getPosition(foodItem.storageLocation)
                spinnerStorageLocation.setSelection(locationPos)

                val unitPos = (spinnerUnit.adapter as ArrayAdapter<String>).getPosition(foodItem.unit)
                spinnerUnit.setSelection(unitPos)

                selectedExpirationDate = foodItem.expirationDate
                textExpirationDate.text = dateFormat.format(selectedExpirationDate)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.add_edit_food_menu, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_save -> {
                saveFoodItem()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun saveFoodItem() {
        val name = binding.editTextName.text.toString().trim()
        val quantityStr = binding.editTextQuantity.text.toString().trim()
        val costStr = binding.editTextCost.text.toString().trim()

        if (name.isEmpty()) {
            binding.editTextName.error = "Name is required"
            return
        }

        val quantity = quantityStr.toIntOrNull() ?: 1
        val cost = costStr.toDoubleOrNull() ?: 0.0

        val foodItem = args.foodItem?.copy(
            name = name,
            category = binding.spinnerCategory.selectedItem.toString(),
            expirationDate = selectedExpirationDate,
            storageLocation = binding.spinnerStorageLocation.selectedItem.toString(),
            quantity = quantity,
            unit = binding.spinnerUnit.selectedItem.toString(),
            estimatedCost = cost,
            notes = binding.editTextNotes.text.toString().trim()
        ) ?: FoodItem(
            name = name,
            category = binding.spinnerCategory.selectedItem.toString(),
            purchaseDate = Date(),
            expirationDate = selectedExpirationDate,
            storageLocation = binding.spinnerStorageLocation.selectedItem.toString(),
            quantity = quantity,
            unit = binding.spinnerUnit.selectedItem.toString(),
            estimatedCost = cost,
            notes = binding.editTextNotes.text.toString().trim()
        )

        if (args.foodItem != null) {
            viewModel.updateFoodItem(foodItem)
        } else {
            viewModel.insertFoodItem(foodItem)
        }

        findNavController().navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}