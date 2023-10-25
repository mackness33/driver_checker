package com.example.driverchecker.ui.viewholders

import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.driverchecker.R
import com.example.driverchecker.machinelearning.data.IGroupMetrics
import com.example.driverchecker.machinelearning.data.IWindowBasicData
import java.util.*

/**
 * Provide a reference to the type of views that you are using
 * (custom ViewHolder)
 */
open class BasicWindowViewHolder(view: View) : RecyclerView.ViewHolder(view) {
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

    open fun bind (detectionItem: Pair<IWindowBasicData, IGroupMetrics<String>?>, onOutputButtonClickListener: (Int) -> Unit) {
        textResult.text = String.format("%s",
            detectionItem.first.supergroup.replaceFirstChar { if (it.isLowerCase()) it.titlecase(
                Locale.getDefault()) else it.toString() }
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