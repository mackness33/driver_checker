package com.example.driverchecker.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.driverchecker.R
import com.example.driverchecker.data.TestEntity

class TestAdapter : ListAdapter<TestEntity, TestAdapter.TestViewHolder>(TestsComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TestViewHolder {
        return TestViewHolder.create(parent)
    }

    override fun onBindViewHolder(holder: TestViewHolder, position: Int) {
        val current = getItem(position)
        holder.bind(current.test)
    }

    class TestViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val wordItemView: TextView = itemView.findViewById(R.id.text_name)

        fun bind(text: String?) {
            wordItemView.text = text
        }

        companion object {
            fun create(parent: ViewGroup): TestViewHolder {
                val view: View = LayoutInflater.from(parent.context)
                    .inflate(R.layout.test_item, parent, false)
                return TestViewHolder(view)
            }
        }
    }

    class TestsComparator : DiffUtil.ItemCallback<TestEntity>() {
        override fun areItemsTheSame(oldItem: TestEntity, newItem: TestEntity): Boolean {
            return oldItem === newItem
        }

        override fun areContentsTheSame(oldItem: TestEntity, newItem: TestEntity): Boolean {
            return oldItem.test == newItem.test
        }
    }
}