package com.example.driverchecker.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.driverchecker.R
import com.example.driverchecker.data.EvaluationEntity

class EvaluationAdapter : ListAdapter<EvaluationEntity, EvaluationAdapter.EvaluationViewHolder>(EvaluationsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EvaluationViewHolder {
        return EvaluationViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: EvaluationViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.name, current.confidence.toString())
    }

    class EvaluationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameItemView: TextView = itemView.findViewById(R.id.text_name)
        private val confidenceItemView: TextView = itemView.findViewById(R.id.text_confidence)

        fun bind(name: String?, confidence: String?) {
            nameItemView.text = name
            confidenceItemView.text = confidence
        }

        companion object {
            fun create(parent: ViewGroup): EvaluationViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.test_item, parent, false)
                return EvaluationViewHolder(view)
            }
        }
    }

    class EvaluationsComparator : DiffUtil.ItemCallback<EvaluationEntity>() {
        override fun areItemsTheSame(oldItem: EvaluationEntity, newItem: EvaluationEntity): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: EvaluationEntity, newItem: EvaluationEntity): Boolean {
            return oldItem.name == newItem.name
        }
    }
}