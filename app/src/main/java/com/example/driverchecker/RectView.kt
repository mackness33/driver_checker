package com.example.driverchecker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

// Copyright (c) 2020 Facebook, Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.
class RectView : View {
    private var paint: Paint
    var size: Pair<Int, Int>
    var maxItems: Int
    var colors: List<Int>?
    var dimensions: Pair<Int, Int>
    private var size_item: Pair<Float, Float>
//    private var offset_item: Pair<Int, Int>


    constructor(context: Context?) : super(context) {
        this.paint = Paint()
        this.size = Pair(0, 0)
        this.maxItems = 1
        this.colors = null
        this.dimensions = Pair(0, 0)
        this.size_item = if (dimensions.first == 0 || dimensions.second == 0) Pair(0.0f, 0.0f) else Pair((size.first/dimensions.first).toFloat(), (size.second/dimensions.second).toFloat())
//        this.offset_item = if (dimensions.first == 0 || dimensions.second == 0) Pair(0, 0) else Pair(size.first%dimensions.first, size.second%dimensions.second)

    }
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        this.paint = Paint()
        this.size = Pair(200, 200)
        this.dimensions = Pair(5, 5)
        this.maxItems = dimensions.first * dimensions.second
        val listColors = ArrayList<Int>()
        listColors.add(Color.BLUE)
        listColors.add(Color.RED)
        listColors.add(Color.GREEN)
        listColors.add(Color.YELLOW)
        this.colors = listColors.toList()
        this.size_item = if (dimensions.first == 0 || dimensions.second == 0) Pair(0.0f, 0.0f) else Pair((size.first/dimensions.first).toFloat(), (size.second/dimensions.second).toFloat())
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        var colorIndex = 0
        for (i: Int in 0 until dimensions.first) {
            for (j: Int in 0 until dimensions.second) {
                paint.color = this.colors!![colorIndex % (colors?.size ?: 1) ]
                canvas.drawRect(
                    size_item.first * i,
                    size_item.second * j,
                    size_item.first * (i + 1),
                    size_item.second * (j + 1),
                    paint
                )

                colorIndex++
            }
        }
    }

    fun updateSize(newSize: Pair<Int, Int>) {
        size = newSize
    }
}