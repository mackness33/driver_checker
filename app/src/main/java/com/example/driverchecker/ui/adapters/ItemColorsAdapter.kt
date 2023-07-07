package com.example.driverchecker.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.driverchecker.ui.views.ClassView
import com.example.driverchecker.R


// items are a list of map with keys the number of the superclass and as value a list of all the classes found

// this adapter is actually the itemColorRecyclerView of the item in predictionAdapter.
// PredictionAdapter display a list of "lines" which are made of various things including the itemColorRecyclerView
//   made of all the classView
// items are a list long as the max number of classes with the color and the
class ItemColorsAdapter(
    private val items: List<Boolean>,
    private var sizeHolder: Pair<Int, Int> = Pair(120, 64),
    private val superclass: Int = 1
) : ColoredAdapter<ItemColorsAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val colorView: ClassView = view.findViewById(R.id.colorItem)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.color_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.itemView.layoutParams = ViewGroup.LayoutParams(sizeHolder.first, sizeHolder.second)
        viewHolder.colorView.updateSize(
            Pair(viewHolder.itemView.layoutParams.width.toFloat(), viewHolder.itemView.layoutParams.height.toFloat())
        )
        viewHolder.colorView.updateColors(colorManager.listFullColors[superclass].scale[position])
        viewHolder.colorView.activate(showColor = items[position])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = items.size
}