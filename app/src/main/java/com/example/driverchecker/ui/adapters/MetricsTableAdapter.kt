package com.example.driverchecker.ui.adapters

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.driverchecker.R
import java.util.*


// items are a list of map with keys the number of the superclass and as value a list of all the classes found
class MetricsTableAdapter(
    private val items: Map<String, Triple<Int, Int, Int>?>
) : ColoredAdapter<MetricsTableAdapter.ViewHolder>() {
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nameView: TextView = view.findViewById(R.id.txtMetricsBodyName)
        val imagesView: TextView = view.findViewById(R.id.txtMetricsBodyTotImages)
        val classesView: TextView = view.findViewById(R.id.txtMetricsBodyTotClasses)
        val objectsView: TextView = view.findViewById(R.id.txtMetricsBodyTotObjects)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.group_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        val mapKey = items.keys.elementAt(position)
        viewHolder.nameView.text = mapKey.replaceFirstChar {
            if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
        }
        viewHolder.nameView.setTextColor(colorManager.listFullColors[position].scale[2])
        viewHolder.imagesView.text = items[mapKey]!!.first.toString()
        viewHolder.classesView.text = items[mapKey]!!.second.toString()
        viewHolder.objectsView.text = items[mapKey]!!.third.toString()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = items.size
}