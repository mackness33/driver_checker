package com.example.driverchecker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModel
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class GridPartialsAdapter(var results : ArrayList<ArrayList<Int>?>) : RecyclerView.Adapter<GridPartialsAdapter.ViewHolder>() {
    private val items: ArrayList<ArrayList<Int>?>? = null
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val recyclerView: RecyclerView = view.findViewById(R.id.rec_view)

        init {
            recyclerView.layoutManager = GridLayoutManager(view.context, 2, RecyclerView.HORIZONTAL, false)
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.grid_partials, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.recyclerView.adapter = GridBoxesAdapter(arrayOf(Color.GREEN, Color.GREEN, Color.GREEN, Color.GREEN))
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = results.size
}