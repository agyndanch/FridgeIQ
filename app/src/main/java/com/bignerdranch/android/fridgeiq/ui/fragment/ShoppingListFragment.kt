package com.bignerdranch.android.fridgeiq.ui.fragment

import android.os.Bundle
import android.view.*
import androidx.core.view.MenuProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.bignerdranch.android.fridgeiq.R
import com.bignerdranch.android.fridgeiq.databinding.FragmentShoppingListBinding
import com.bignerdranch.android.fridgeiq.ui.adapter.ShoppingListAdapter
import com.bignerdranch.android.fridgeiq.ui.viewmodel.ShoppingListViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.launch

class ShoppingListFragment : Fragment() {

    private var _binding: FragmentShoppingListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: ShoppingListViewModel by viewModels()
    private lateinit var adapter: ShoppingListAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentShoppingListBinding.inflate(inflater, container, false)
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
                menuInflater.inflate(R.menu.shopping_list_menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    R.id.action_clear_purchased -> {
                        MaterialAlertDialogBuilder(requireContext())
                            .setTitle("Clear Purchased Items")
                            .setMessage("Remove all purchased items from the list?")
                            .setPositiveButton("Clear") { _, _ ->
                                viewModel.clearPurchasedItems()
                            }
                            .setNegativeButton("Cancel", null)
                            .show()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    private fun setupRecyclerView() {
        adapter = ShoppingListAdapter(
            onItemClick = { item ->
                val action = ShoppingListFragmentDirections.actionShoppingListToAddEditShoppingItem(item)
                findNavController().navigate(action)
            },
            onItemChecked = { item, isChecked ->
                viewModel.updateShoppingItem(item.copy(isPurchased = isChecked))
            }
        )

        binding.recyclerViewShoppingList.adapter = adapter
        binding.recyclerViewShoppingList.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.allShoppingItems.collect { items ->
                    adapter.submitList(items)
                    binding.textEmptyState.visibility = if (items.isEmpty()) View.VISIBLE else View.GONE
                }
            }
        }
    }

    private fun setupFab() {
        binding.fabAddItem.setOnClickListener {
            findNavController().navigate(R.id.action_shopping_list_to_add_edit_shopping_item)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}