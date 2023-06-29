package com.example.driverchecker

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlin.math.round
import kotlin.math.sqrt


// items are a list of map with keys the number of the superclass and as value a list of all the classes found
class PartialsAdapter(val items: List<Pair<Int, List<Int>>>, maxClasses:Int = 2, private var sizeHolder: Int = 50) : ColoredAdapter<PartialsAdapter.ViewHolder>() {
    private val dimension: Int
    private val colorClasses: Map<Int, List<Int>>

    init {
        val square = round(sqrt(maxClasses.toDouble()))
        dimension = when {
            maxClasses <= 0 -> 0
            maxClasses == 1 -> 1
            maxClasses < 4 -> 2
            maxClasses % square > 0 -> square+1
            else -> square
        }.toInt()
        colorClasses = mapOf(1 to listOf(Color.GREEN, Color.CYAN, Color.BLUE))
    }

    /**
//        viewHolder.predictionView.invalidate()first
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val predictionView: RectView = view.findViewById(R.id.rect_item)
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.prediction_item, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.predictionView.updateDimensions(Pair(dimension, dimension))
//        val params = viewHolder.itemView.layoutParams
        viewHolder.itemView.layoutParams = ViewGroup.LayoutParams(sizeHolder, sizeHolder)
        viewHolder.predictionView.updateSize(
            Pair(viewHolder.itemView.layoutParams.width, viewHolder.itemView.layoutParams.height)
        )
        viewHolder.predictionView.updateColors(colorManager.listColor[items[position].first])
        viewHolder.predictionView.updateSelectedClasses(items[position].second)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = items.size
}