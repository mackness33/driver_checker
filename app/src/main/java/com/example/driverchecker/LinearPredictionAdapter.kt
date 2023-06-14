package com.example.driverchecker

import android.graphics.Rect
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView

// display a prediction
class LinearPredictionAdapter(var boxes : IntArray) : RecyclerView.Adapter<LinearPredictionAdapter.ViewHolder>() {
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val boxesView: MutableList<RectView> = mutableListOf<RectView>()

        init {
            // Define click listener for the ViewHolder's View
            boxesView.add(view.findViewById(R.id.rectView1))
            boxesView.add(view.findViewById(R.id.rectView2))
            boxesView.add(view.findViewById(R.id.rectView3))
            boxesView.add(view.findViewById(R.id.rectView4))
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.grid_boxes, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.boxesView[position].color = boxes[position]
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = boxes.size
}