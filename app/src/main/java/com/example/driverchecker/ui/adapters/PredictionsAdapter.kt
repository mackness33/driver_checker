package com.example.driverchecker.ui.adapters

import android.graphics.Bitmap
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.driverchecker.utils.BitmapUtils
import com.example.driverchecker.R
import com.example.driverchecker.machinelearning.data.IImageDetectionFullOutput
import com.example.driverchecker.utils.ColorManager
import java.util.*


// items are a list of map with keys the number of the superclass and as value a list of all the classes found

// this adapter is actually the itemColorRecyclerView of the item in predictionAdapter.
// PredictionAdapter display a list of "lines" which are made of various things including the itemColorRecyclerView
//   made of all the classView
class PredictionsAdapter(
    private val items: List<IImageDetectionFullOutput<String>>,
    private var colorList: Set<String>? = setOf("driver", "passenger")
) : RecyclerView.Adapter<PredictionsAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textIndex: TextView = view.findViewById(R.id.txtIndex)
        val textGroup: TextView = view.findViewById(R.id.txtGroup)
        val imageInput: ImageView = view.findViewById(R.id.imgInput)

        fun bind (detectionItem: IImageDetectionFullOutput<String>, position: Int) {
            val bitmap: Bitmap = BitmapUtils.rotateBitmap(detectionItem.input.input, -90.0f)
            imageInput.setImageBitmap(bitmap)
            textIndex.text = position.toString()
            textGroup.text = detectionItem.groups.keys.first().replaceFirstChar {
                if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString()
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_output, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.bind(items[position], position)
        var indexOfGroup = colorList?.indexOfFirst { it.contentEquals(items[position].groups.keys.first()) }
        indexOfGroup = if (indexOfGroup == null || indexOfGroup < 0) 6 else indexOfGroup

        viewHolder.textGroup.setTextColor(ColorManager.listFullColors[indexOfGroup].scale[2])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = items.size
}