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
import com.example.driverchecker.machinelearning.data.IGroupMetrics
import com.example.driverchecker.machinelearning.data.IImageDetectionFinalResult
import com.example.driverchecker.machinelearning.data.IImageDetectionFullOutput
import com.example.driverchecker.machinelearning.data.IWindowBasicData
import com.example.driverchecker.utils.ColorManager
import java.util.*


// items are a list of map with keys the number of the superclass and as value a list of all the classes found

// this adapter is actually the itemColorRecyclerView of the item in predictionAdapter.
// PredictionAdapter display a list of "lines" which are made of various things including the itemColorRecyclerView
//   made of all the classView
class WindowsAdapter(
    private val items: Map<IWindowBasicData, IGroupMetrics<String>?>,
    private var colorList: Set<String>? = setOf("driver", "passenger")
) : RecyclerView.Adapter<WindowsAdapter.ViewHolder>() {

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val textResult: TextView = view.findViewById(R.id.text_result)
        val textFrame: TextView = view.findViewById(R.id.text_frame)
        val textThreshold: TextView = view.findViewById(R.id.text_threshold)
        val textConfidence: TextView = view.findViewById(R.id.text_confidence)
        val textType: TextView = view.findViewById(R.id.text_type)
        val textTotalTime: TextView = view.findViewById(R.id.text_total_time)
        val textTotalWindows: TextView = view.findViewById(R.id.text_total_frames)
        val textDriverTotalImages: TextView = view.findViewById(R.id.text_images_driver)
        val textDriverTotalClasses: TextView = view.findViewById(R.id.text_classes_driver)
        val textDriverTotalObjects: TextView = view.findViewById(R.id.text_objects_driver)
        val textPassengerTotalImages: TextView = view.findViewById(R.id.text_images_passenger)
        val textPassengerTotalClasses: TextView = view.findViewById(R.id.text_classes_passenger)
        val textPassengerTotalObjects: TextView = view.findViewById(R.id.text_objects_passenger)

        fun bind (detectionItem: Pair<IWindowBasicData, IGroupMetrics<String>?>) {
            textResult.text = String.format("%s",
                detectionItem.first.supergroup.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
            )
            textFrame.text = detectionItem.first.windowFrames.toString()
            textThreshold.text = String.format("%.2f%%", detectionItem.first.windowThreshold.times(100))
            textConfidence.text = String.format("%.2f%%", detectionItem.first.confidence.times(100))
            textType.text = detectionItem.first.type
            textTotalTime.text = String.format("%.2fs", detectionItem.first.totalTime)
            textTotalWindows.text = detectionItem.first.totalWindows.toString()

            detectionItem.second?.groupMetrics?.forEach { entry ->
                when (entry.key) {
                    "driver" -> {
                        textDriverTotalImages.text = entry.value.first.toString()
                        textDriverTotalClasses.text = entry.value.second.toString()
                        textDriverTotalObjects.text = entry.value.third.toString()
                    }
                    "passenger" -> {
                        textPassengerTotalImages.text = entry.value.first.toString()
                        textPassengerTotalClasses.text = entry.value.second.toString()
                        textPassengerTotalObjects.text = entry.value.third.toString()
                    }
                }
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        // Create a new view, which defines the UI of the list item
        val view = LayoutInflater.from(viewGroup.context)
            .inflate(R.layout.item_window, viewGroup, false)

        return ViewHolder(view)
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.bind(items.toList()[position])
//        var indexOfGroup = colorList?.indexOfFirst { it.contentEquals(items[position].groups.keys.first()) }
//        indexOfGroup = if (indexOfGroup == null || indexOfGroup < 0) 6 else indexOfGroup
//
//        viewHolder.textGroup.setTextColor(ColorManager.listFullColors[indexOfGroup].scale[2])
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = items.size
}