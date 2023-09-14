package com.example.driverchecker.ui.adapters

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.driverchecker.R
import com.example.driverchecker.ui.views.RectView
import com.example.driverchecker.utils.ColorManager
import kotlin.math.round
import kotlin.math.sqrt


// items are a list of map with keys the number of the superclass and as value a list of all the classes found
class PartialsAdapter(
    private val items: List<Map<String, Set<Int>>>,
    maxClasses:Int = 2,
    private var sizeHolder: Int = 50,
) : RecyclerView.Adapter<PartialsAdapter.ViewHolder>() {
    private val dimension: Int
    private var colorList: Set<String>? = null

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
            .inflate(R.layout.item_partial, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.predictionView.updateDimensions(Pair(dimension, dimension))
        viewHolder.itemView.layoutParams = ViewGroup.LayoutParams(sizeHolder, sizeHolder)
        viewHolder.predictionView.updateSize(
            Pair(viewHolder.itemView.layoutParams.width, viewHolder.itemView.layoutParams.height)
        )
        
        try {
            val group = items[position].toList().first()

            var indexOfGroup = colorList?.indexOfFirst { it.contentEquals(group.first) }
            indexOfGroup = if (indexOfGroup == null || indexOfGroup < 0) 9 else indexOfGroup
            viewHolder.predictionView.updateColors(ColorManager.listFullColors[indexOfGroup ?: 0])
            viewHolder.predictionView.updateSelectedClasses(group.second.toList())
        } catch (e: Throwable) {
            Log.e("PartialsAdapter", e.message.toString(), e)
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = items.size

    fun updateGroupList(groups: Set<String>) {
        colorList = groups
    }
}