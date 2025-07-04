package com.bignerdranch.android.fridgeiq.ui.util

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.bignerdranch.android.fridgeiq.R

abstract class SwipeToDeleteCallback(context: Context) : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

    private val deleteIcon: Drawable = ContextCompat.getDrawable(context, R.drawable.ic_delete_24)!!
    private val background: ColorDrawable = ColorDrawable(Color.RED)

    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder
    ): Boolean {
        return false
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dX: Float,
        dY: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ) {
        val itemView = viewHolder.itemView
        val iconMargin = (itemView.height - deleteIcon.intrinsicHeight) / 2

        background.setBounds(
            itemView.right + dX.toInt(),
            itemView.top,
            itemView.right,
            itemView.bottom
        )
        background.draw(c)

        deleteIcon.setBounds(
            itemView.right - iconMargin - deleteIcon.intrinsicWidth,
            itemView.top + iconMargin,
            itemView.right - iconMargin,
            itemView.bottom - iconMargin
        )
        deleteIcon.draw(c)

        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
    }
}