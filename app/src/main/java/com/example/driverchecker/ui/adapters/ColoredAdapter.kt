package com.example.driverchecker.ui.adapters

import androidx.recyclerview.widget.RecyclerView
import com.example.driverchecker.ColorManager

abstract class ColoredAdapter<ViewHolder : RecyclerView.ViewHolder?> : RecyclerView.Adapter<ViewHolder>() {
    val colorManager: ColorManager = ColorManager()
}
