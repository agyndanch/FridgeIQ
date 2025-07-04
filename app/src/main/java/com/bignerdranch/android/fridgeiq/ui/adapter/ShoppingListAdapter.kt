package com.bignerdranch.android.fridgeiq.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.fridgeiq.data.entity.ShoppingListItem
import com.bignerdranch.android.fridgeiq.databinding.ItemShoppingListBinding

class ShoppingListAdapter(
    private val onItemClick: (ShoppingListItem) -> Unit,
    private val onItemChecked: (ShoppingListItem, Boolean) -> Unit
) : ListAdapter<ShoppingListItem, ShoppingListAdapter.ShoppingListViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ShoppingListViewHolder {
        val binding = ItemShoppingListBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ShoppingListViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ShoppingListViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class ShoppingListViewHolder(private val binding: ItemShoppingListBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ShoppingListItem) {
            binding.apply {
                textItemName.text = item.name
                textCategory.text = item.category
                "${item.quantity} ${item.unit}".also { textQuantity.text = it }

                checkboxPurchased.isChecked = item.isPurchased
                checkboxPurchased.setOnCheckedChangeListener { _, isChecked ->
                    onItemChecked(item, isChecked)
                }

                root.setOnClickListener { onItemClick(item) }
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<ShoppingListItem>() {
            override fun areItemsTheSame(oldItem: ShoppingListItem, newItem: ShoppingListItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: ShoppingListItem, newItem: ShoppingListItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}