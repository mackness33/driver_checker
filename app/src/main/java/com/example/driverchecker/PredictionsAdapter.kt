package com.example.driverchecker

import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.driverchecker.machinelearning.imagedetection.ImageDetectionArrayResult
import kotlin.math.round
import kotlin.math.sqrt


// items are a list of map with keys the number of the superclass and as value a list of all the classes found
class PredictionsAdapter(
    val items: List<ImageDetectionArrayResult>,
    private var maxClasses:Int = 2,
    private var sizeHolder: Pair<Int, Int> = Pair(120, 64)
) : RecyclerView.Adapter<PredictionsAdapter.ViewHolder>() {
    private val colorClasses: Map<Int, List<Int>>

    init {
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
        viewHolder.predictionView.updateDimensions(Pair(maxClasses, 1))
//        val params = viewHolder.itemView.layoutParams
        viewHolder.itemView.layoutParams = ViewGroup.LayoutParams(sizeHolder.first, sizeHolder.second)
        viewHolder.predictionView.updateSize(
            Pair(viewHolder.itemView.layoutParams.width, viewHolder.itemView.layoutParams.height)
        )
//        TODO()
//        viewHolder.predictionView.updateColors(colorClasses.getValue(items[position].first))
//        viewHolder.predictionView.updateSelectedClasses(items[position].second)
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = items.size
}