package com.example.driverchecker.ui.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View

// Copyright (c) 2020 Facebook, Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.
class ClassView(context: Context?, attrs: AttributeSet?) : View(context, attrs) {
    private var paint: Paint
    var size: Pair<Float, Float>
        private set
    var offset: Pair<Float, Float>
        private set
    var color: Int?
        private set
    var isActive: Boolean
        private set


    init {
        paint = Paint()
        size = Pair(0.0f, 0.0f)
        offset = Pair(0.0f, 0.0f)
        color = Color.BLUE
        isActive = false
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (isActive && size.first > 0 && size.second > 0) {
            canvas.drawRect(
                offset.first,
                offset.second,
                offset.first * size.first,
                offset.second * size.second,
                paint
            )
        }
    }

    fun updateSize (newSize: Pair<Float, Float>) {
        size = newSize
    }

    fun updateOffset (newOffset: Pair<Float, Float>) {
        offset = newOffset
    }

    fun updateColors(newColor: Int) {
        color = newColor
    }

    fun activate (showColor: Boolean) {
        isActive = showColor
    }
}