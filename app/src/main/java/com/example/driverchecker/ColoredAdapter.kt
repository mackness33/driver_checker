package com.example.driverchecker

import android.graphics.Color
import androidx.recyclerview.widget.RecyclerView

abstract class ColoredAdapter<ViewHolder : RecyclerView.ViewHolder?> : RecyclerView.Adapter<ViewHolder>() {
    val colorManager: ColorManager = ColorManager()
}
