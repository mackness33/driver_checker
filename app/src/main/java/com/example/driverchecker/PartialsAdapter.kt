package com.example.driverchecker

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.driverchecker.machinelearning.imagedetection.ImageDetectionArrayResult
import kotlin.math.round
import kotlin.math.sqrt


class PartialsAdapter(size: Int = 2, var sizeHolder: Int = 100) : RecyclerView.Adapter<PartialsAdapter.ViewHolder>() {
    private val items: ImageDetectionArrayResult?
    private val dimension: Int

    init {
        items = ImageDetectionArrayResult(size)
        val square = round(sqrt(size.toDouble()))
        dimension = (if (size % square > 0) square+1 else square).toInt()
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val predictionView: RectView = view.findViewById(R.id.rec_view)
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
        viewHolder.predictionView.updateSize(Pair(sizeHolder, sizeHolder))

        viewHolder.predictionView.invalidate()
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = items?.size ?: 0
}