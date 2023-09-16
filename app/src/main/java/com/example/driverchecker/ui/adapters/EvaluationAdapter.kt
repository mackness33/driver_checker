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
import com.example.driverchecker.database.entity.EvaluationEntity
import com.example.driverchecker.utils.ColorManager
import com.example.driverchecker.utils.IColorScale

class EvaluationAdapter (
    private val onClickItemListener: (Long) -> Unit,
    private val onClickDeleteListener: (Long) -> Unit,
    private var colorList: Set<String>? = null
) : ListAdapter<EvaluationEntity, EvaluationAdapter.EvaluationViewHolder>(EvaluationsComparator()) {

    init {
        // TODO: make the set of name of groups automatic
        colorList = setOf("driver", "passenger")
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EvaluationViewHolder {
        return EvaluationViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: EvaluationViewHolder, position: Int) {
        val current = getItem(position)
        var indexOfGroup = colorList?.indexOfFirst { it.contentEquals(current.supergroup) }
        indexOfGroup = if (indexOfGroup == null || indexOfGroup < 0) 6 else indexOfGroup

        holder.bind(current, onClickItemListener, onClickDeleteListener, ColorManager.listFullColors[indexOfGroup])
    }

    class EvaluationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val nameItemView: TextView = itemView.findViewById(R.id.text_name)
        private val confidenceItemView: TextView = itemView.findViewById(R.id.text_confidence)
        private val deleteButton: Button = itemView.findViewById(R.id.button_delete)

        fun bind(evaluation: EvaluationEntity, itemListener: (Long) -> Unit, deleteListener: (Long) -> Unit, colorScheme: IColorScale) {
            nameItemView.text = evaluation.name
            confidenceItemView.text = evaluation.confidence.toString()
            deleteButton.setOnClickListener { deleteListener(evaluation.id) }
            itemView.setOnClickListener { itemListener(evaluation.id) }
            itemView.setBackgroundColor(colorScheme.scale[4])
        }

        companion object {
            fun create(parent: ViewGroup): EvaluationViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.item_evaluation, parent, false)
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