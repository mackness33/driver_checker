package com.example.driverchecker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import android.widget.Adapter
import android.widget.BaseAdapter
import android.widget.GridView

// Copyright (c) 2020 Facebook, Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.
class RectGroupView(
    context: Context?,
    attrs: AttributeSet?,
    size: Pair<Float, Float> = Pair(0.0f, 0.0f),
    position: Pair<Float, Float> = Pair(0.0f, 0.0f),
    private var stroke: Float = 0.0f,
    color: Int = Color.GREEN,
    customAdapter: BaseAdapter
) : GridView(context, attrs) {
    var paint: Paint = Paint()
    var rect: RectF

    init {
        this.paint.color = color
        this.rect = RectF(
            (position.first - size.first / 2),
            (position.second - size.second / 2),
            (position.first + size.first / 2),
            (position.second + size.second / 2)
        )
        super.setAdapter(customAdapter)
    }

    override fun onDraw(canvas: Canvas) {
        if (stroke != 0.0f) {
            paint.strokeWidth = stroke
            canvas.drawRect(rect, paint)
        }
    }
}