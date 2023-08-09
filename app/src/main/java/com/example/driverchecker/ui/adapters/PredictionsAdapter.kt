package com.example.driverchecker.ui.adapters

import android.graphics.Bitmap
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.driverchecker.R
import com.example.driverchecker.machinelearning.data.IImageDetectionResultOld


// items are a list of map with keys the number of the superclass and as value a list of all the classes found

// this adapter is actually the itemColorRecyclerView of the item in predictionAdapter.
// PredictionAdapter display a list of "lines" which are made of various things including the itemColorRecyclerView
//   made of all the classView
class PredictionsAdapter(
    private val items: List<IImageDetectionResultOld<String>>,
    private var sizeHolder: Pair<Int, Int> = Pair(120, 64),
    private val maxClassesPerSuperclass: Int = 0
) : ColoredAdapter<PredictionsAdapter.ViewHolder>() {
    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textIndex: TextView = view.findViewById(R.id.txtIndex)
        val textGroup: TextView = view.findViewById(R.id.txtGroup)
        val imageInput: ImageView = view.findViewById(R.id.imgInput)
        val colorGroupView: RecyclerView = view.findViewById(R.id.color_class_view)

        init {
            colorGroupView.layoutManager = LinearLayoutManager(view.context, RecyclerView.HORIZONTAL, true)
            colorGroupView.itemAnimator = null
        }
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

        val indexFoundClass = items[position].listItems
            .distinctBy { predictions -> predictions.classIndex }
            .map { prediction -> prediction.classIndex}
        val foundClass: List<Boolean> = MutableList(maxClassesPerSuperclass) { index -> indexFoundClass.contains(index) }
        viewHolder.textIndex.text = items[position].listItems.first().classification.index.toString()
        viewHolder.textGroup.text = items[position].groups.first()
        viewHolder.textGroup.setTextColor(colorManager.listFullColors[1].main ?: Color.BLACK)
        viewHolder.imageInput.setImageBitmap(
            Bitmap.createScaledBitmap(
                items[position].data.data,
                viewHolder.imageInput.maxWidth,
                viewHolder.imageInput.maxHeight,
                true
            )
        )
        viewHolder.colorGroupView.adapter = ItemColorsAdapter(foundClass)
//        viewHolder.itemView.layoutParams = ViewGroup.LayoutParams(sizeHolder.first, sizeHolder.second)
//        viewHolder.predictionView.updateSize(
//            Pair(viewHolder.itemView.layoutParams.width.toFloat(), viewHolder.itemView.layoutParams.height.toFloat())
//        )
//        viewHolder.predictionView.updateColors(colorManager.listColor[superclass].scale[position])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = items.size
}