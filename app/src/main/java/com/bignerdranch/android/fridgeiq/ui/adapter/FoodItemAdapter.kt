package com.bignerdranch.android.fridgeiq.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.fridgeiq.data.entity.FoodItem
import com.bignerdranch.android.fridgeiq.databinding.ItemFoodBinding
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.math.abs

class FoodItemAdapter(
    private val onItemClick: (FoodItem) -> Unit,
    private val onMarkConsumed: (FoodItem) -> Unit,
    private val onMarkWasted: (FoodItem) -> Unit
) : ListAdapter<FoodItem, FoodItemAdapter.FoodItemViewHolder>(DiffCallback) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FoodItemViewHolder {
        val binding = ItemFoodBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FoodItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: FoodItemViewHolder, position: Int) {
        val currentItem = getItem(position)
        holder.bind(currentItem)
    }

    inner class FoodItemViewHolder(private val binding: ItemFoodBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(foodItem: FoodItem) {
            binding.apply {
                textFoodName.text = foodItem.name
                textCategory.text = foodItem.category
                "${foodItem.quantity} ${foodItem.unit}".also { textQuantity.text = it }
                textStorageLocation.text = foodItem.storageLocation

                val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                textExpirationDate.text = dateFormat.format(foodItem.expirationDate)

                // Calculate days until expiration
                val today = Date()
                val daysUntilExpiration = TimeUnit.DAYS.convert(
                    foodItem.expirationDate.time - today.time,
                    TimeUnit.MILLISECONDS
                )

                textDaysLeft.text = when {
                    daysUntilExpiration < 0 -> "Expired ${abs(daysUntilExpiration)} days ago"
                    daysUntilExpiration == 0L -> "Expires today"
                    daysUntilExpiration == 1L -> "Expires tomorrow"
                    else -> "Expires in $daysUntilExpiration days"
                }

                // Set color based on expiration status
                val context = root.context
                when {
                    daysUntilExpiration < 0 -> {
                        cardView.setCardBackgroundColor(context.getColor(android.R.color.holo_red_light))
                    }
                    daysUntilExpiration <= 2 -> {
                        cardView.setCardBackgroundColor(context.getColor(android.R.color.holo_orange_light))
                    }
                    else -> {
                        cardView.setCardBackgroundColor(context.getColor(android.R.color.white))
                    }
                }

                root.setOnClickListener { onItemClick(foodItem) }
                buttonMarkConsumed.setOnClickListener { onMarkConsumed(foodItem) }
                buttonMarkWasted.setOnClickListener { onMarkWasted(foodItem) }
            }
        }
    }

    companion object {
        private val DiffCallback = object : DiffUtil.ItemCallback<FoodItem>() {
            override fun areItemsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: FoodItem, newItem: FoodItem): Boolean {
                return oldItem == newItem
            }
        }
    }
}