package com.example.driverchecker.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.driverchecker.R
import com.example.driverchecker.data.EvaluationEntity

class EvaluationAdapter (
    private val onClickItemListener: (Int) -> Unit,
    private val onClickDeleteListener: (Int) -> Unit
) : ListAdapter<EvaluationEntity, EvaluationAdapter.EvaluationViewHolder>(EvaluationsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EvaluationViewHolder {
        return EvaluationViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: EvaluationViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current, onClickItemListener, onClickDeleteListener)
    }

    class EvaluationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameItemView: TextView = itemView.findViewById(R.id.text_name)
        private val confidenceItemView: TextView = itemView.findViewById(R.id.text_confidence)
        private val deleteButton: Button = itemView.findViewById(R.id.button_delete)

        fun bind(evaluation: EvaluationEntity, itemListener: (Int) -> Unit, deleteListener: (Int) -> Unit ) {
            nameItemView.text = evaluation.name
            confidenceItemView.text = evaluation.confidence.toString()
            deleteButton.setOnClickListener { deleteListener(evaluation.id) }
            itemView.setOnClickListener { itemListener(evaluation.id) }
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