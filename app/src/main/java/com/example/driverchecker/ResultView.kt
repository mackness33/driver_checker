package com.example.driverchecker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.driverchecker.machinelearning.data.ImageDetectionBox

// Copyright (c) 2020 Facebook, Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.
class ResultView : View {
    private var paintRectangle: Paint? = null
    private var paintText: Paint? = null
    private var path: Path? = null
    private var rect: RectF? = null
    private var results: ArrayList<ImageDetectionBox>? = null

    constructor(context: Context?) : super(context) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        paintRectangle = Paint()
        paintRectangle!!.color = Color.GREEN
        paintText = Paint()
        path = Path()
        rect = RectF()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (results == null) return

        for (result: ImageDetectionBox in results!!) {
            paintRectangle!!.strokeWidth = 5f
            paintRectangle!!.style = Paint.Style.STROKE
            canvas.drawRect(result.rect, paintRectangle!!)
            path?.reset()
            rect?.set(
                result.rect.left.toFloat(),
                result.rect.top.toFloat(),
                (result.rect.left + TEXT_WIDTH).toFloat(),
                (result.rect.top + TEXT_HEIGHT).toFloat()
            )
            path?.addRect(rect!!, Path.Direction.CW)
            paintText!!.color = Color.MAGENTA
            canvas.drawPath(path!!, paintText!!)
            paintText!!.color = Color.WHITE
            paintText!!.strokeWidth = 0f
            paintText!!.style = Paint.Style.FILL
            paintText!!.textSize = 32f
            canvas.drawText(
                String.format(
                    "%s %.2f",
//                    PrePostProcessor.mClasses.get(result.classIndex),
                    "Not yet",
                    result.score
                ),
                result.rect.left + TEXT_X,
                result.rect.top + TEXT_Y,
                paintText!!
            )
        }
    }

    fun setResults(results: ArrayList<ImageDetectionBox>?) {
        this.results = results
    }

    companion object {
        private const val TEXT_X = 40
        private const val TEXT_Y = 35
        private const val TEXT_WIDTH = 260
        private const val TEXT_HEIGHT = 50
    }
}