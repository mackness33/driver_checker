package com.example.driverchecker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.driverchecker.machinelearning.imagedetection.ImageDetectionArrayResult
import kotlin.math.round
import kotlin.math.sqrt


class PartialsAdapter(val items: List<ImageDetectionArrayResult>, maxClasses:Int = 2, private var sizeHolder: Int = 50) : RecyclerView.Adapter<PartialsAdapter.ViewHolder>() {
    private val dimension: Int

    init {
        val square = round(sqrt(maxClasses.toDouble()))
        dimension = when {
            maxClasses <= 0 -> 0
            maxClasses == 1 -> 1
            maxClasses < 4 -> 2
            maxClasses % square > 0 -> square+1
            else -> square
        }.toInt()
    }

    /**
//        viewHolder.predictionView.invalidate()
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
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = items.size
}