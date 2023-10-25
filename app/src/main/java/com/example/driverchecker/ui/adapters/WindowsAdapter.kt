package com.example.driverchecker.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.driverchecker.R
import com.example.driverchecker.machinelearning.data.IGroupMetrics
import com.example.driverchecker.machinelearning.data.IWindowBasicData
import com.example.driverchecker.ui.viewholders.BasicWindowViewHolder
import com.example.driverchecker.ui.viewholders.OffsetWindowViewHolder
import java.util.*


// items are a list of map with keys the number of the superclass and as value a list of all the classes found

// this adapter is actually the itemColorRecyclerView of the item in predictionAdapter.
// PredictionAdapter display a list of "lines" which are made of various things including the itemColorRecyclerView
//   made of all the classView
class WindowsAdapter(
    private val items: Map<IWindowBasicData, IGroupMetrics<String>?>,
    private val colorList: Set<String>? = setOf("driver", "passenger"),
    private val onPartialClickListener: (Int) -> Unit
) : RecyclerView.Adapter<BasicWindowViewHolder>() {

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
        val buttonCheckImages: Button = view.findViewById((R.id.button_check_images))

        fun bind (detectionItem: Pair<IWindowBasicData, IGroupMetrics<String>?>, onOutputButtonClickListener: (Int) -> Unit) {
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

            buttonCheckImages.setOnClickListener { _ ->
                onOutputButtonClickListener(detectionItem.first.totalWindows + detectionItem.first.windowFrames - 1)
            }
        }
    }

    // Create new views (invoked by the layout manager)
    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): BasicWindowViewHolder {
        // Create a new view, which defines the UI of the list item
        return when (viewType){
            Const.HAS_OFFSET -> OffsetWindowViewHolder(
                LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_offset_window, viewGroup, false)
            )
            else -> BasicWindowViewHolder(
                LayoutInflater.from(viewGroup.context)
                .inflate(R.layout.item_window, viewGroup, false)
            )
        }
    }

    // Replace the contents of a view (invoked by the layout manager)
    override fun onBindViewHolder(viewHolder: BasicWindowViewHolder, position: Int) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.bind(items.toList()[position], onPartialClickListener)
//        var indexOfGroup = colorList?.indexOfFirst { it.contentEquals(items[position].groups.keys.first()) }
//        indexOfGroup = if (indexOfGroup == null || indexOfGroup < 0) 6 else indexOfGroup
//
//        viewHolder.textGroup.setTextColor(ColorManager.listFullColors[indexOfGroup].scale[2])
    }

    private object Const{
        const val HAS_OFFSET = 0 // random unique value
        const val NO_OFFSET = 1
    }

    override fun getItemViewType(position: Int): Int {
        return if (items.toList()[position].first.type.contains("offset", true))
            Const.HAS_OFFSET
        else
            Const.NO_OFFSET
    }

    // Return the size of your dataset (invoked by the layout manager)
    override fun getItemCount() = items.size
}