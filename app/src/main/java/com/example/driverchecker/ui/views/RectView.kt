package com.example.driverchecker.ui.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.driverchecker.utils.ColorScale
import com.example.driverchecker.utils.IColorScale

// Copyright (c) 2020 Facebook, Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.
class RectView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var paint: Paint
    var size: Pair<Int, Int>
    var maxItems: Int
        private set
    var colors: IColorScale?
        private set
    var dimensions: Pair<Int, Int>
        private set
    private var size_item: Pair<Float, Float>
    var selectedClasses: List<Int>?
        private set
//    private var offset_item: Pair<Int, Int>
    private var sizeHolder: Int = 50

    init {
        this.paint = Paint()
        this.size = Pair(0, 0)
        this.dimensions = Pair(0, 0)
        this.maxItems = 0
        this.colors = ColorScale(listOf (
            Color.BLUE,
            Color.RED,
            Color.GREEN,
            Color.YELLOW
        ))
        this.size_item = updateSizeItem()
        this.selectedClasses = null
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var colorIndex = 0
//        for (i: Int in 0 until dimensions.first) {
//            for (j: Int in 0 until dimensions.second) {
//                paint.color = this.colors!![colorIndex % (colors?.size ?: 1) ]
//                canvas.drawRect(
//                    size_item.first * i,
//                    size_item.second * j,
//                    size_item.first * (i + 1),
//                    size_item.second * (j + 1),
//                    paint
//                )
//
//                colorIndex++
//            }
//        }

        if (selectedClasses != null && dimensions.first != 0 && dimensions.second != 0) {
            var x = 0
            var y = 0
            for (predictionClass in selectedClasses!!) {
                paint.color = colors!!.scale[predictionClass % (colors?.scale?.size ?: 1) ]
//                paint.color = Color.toArgb(Color.BLUE.toLong())
                x = predictionClass % dimensions.first
                y = (predictionClass / dimensions.first) % dimensions.second
                canvas.drawRect(
                    size_item.first * x ,
                    size_item.second * y,
                    size_item.first * (x + 1),
                    size_item.second * (y + 1),
                    paint
                )
            }
        }
    }

    private fun updateSizeItem () : Pair<Float, Float> {
        return if (dimensions.first == 0 || dimensions.second == 0) Pair(0.0f, 0.0f) else Pair((size.first/dimensions.first).toFloat(), (size.second/dimensions.second).toFloat())
    }

    fun updateDimensions (dim: Pair<Int, Int>) {
        dimensions = dim
        maxItems = dimensions.first * dimensions.second
        size_item = updateSizeItem()
    }

    fun updateSize(newSize: Pair<Int, Int>) {
        size = newSize
        size_item = updateSizeItem()
    }

    fun updateColors(newColors: IColorScale) {
        colors = newColors
    }

    fun updateSelectedClasses(newClasses: List<Int>) {
        selectedClasses = newClasses
    }
}