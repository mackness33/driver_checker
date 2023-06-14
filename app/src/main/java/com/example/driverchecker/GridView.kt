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
class GridView(
    context: Context?,
    attrs: AttributeSet?,
    color: Int = Color.GREEN,
    var size: Pair<Float, Float> = Pair(0.0f, 0.0f),
    var position: Pair<Float, Float> = Pair(0.0f, 0.0f)
) : View(context, attrs) {
    var paint: Paint
    var rect: RectF

    init {
        this.paint = Paint()
        this.paint.color = color
        this.rect = RectF(
            (position.first - size.first / 2),
            (position.second - size.second / 2),
            (position.first + size.first / 2),
            (position.second + size.second / 2)
        )
    }

    override fun onDraw(canvas: Canvas) {
        paint.strokeWidth = 0.0f
        canvas.drawRect(rect, paint)
    }
}