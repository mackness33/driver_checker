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
open class OffsetWindowViewHolder(view: View) : BasicWindowViewHolder(view) {
    val textOffset: TextView = view.findViewById(R.id.text_offset)

    override fun bind (detectionItem: Pair<IWindowBasicData, IGroupMetrics<String>?>, onOutputButtonClickListener: (Int) -> Unit) {
        textOffset.text = detectionItem.first.offset.toString()
        super.bind(detectionItem, onOutputButtonClickListener)
    }
}