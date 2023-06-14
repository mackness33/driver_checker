package com.example.driverchecker

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.driverchecker.machinelearning.data.ImageDetectionBox
import com.example.driverchecker.machinelearning.imagedetection.ImageDetectionArrayResult
import com.example.driverchecker.machinelearning.imagedetection.ImageDetectionResult

// Copyright (c) 2020 Facebook, Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.
class RectView : View {
    private var paintRectangle: Paint? = null
    private var paintText: Paint? = null
    private var path: Path? = null
    private var rect: RectF? = null
    private var results: ImageDetectionArrayResult? = null

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

        val res: ImageDetectionArrayResult = results!!

        for (box: ImageDetectionResult in res) {
            paintRectangle!!.strokeWidth = 5f
            paintRectangle!!.style = Paint.Style.STROKE
            canvas.drawRect(box.result.rect, paintRectangle!!)
            path?.reset()
            rect?.set(
                box.result.rect.left.toFloat(),
                box.result.rect.top.toFloat(),
                (box.result.rect.left + TEXT_WIDTH).toFloat(),
                (box.result.rect.top + TEXT_HEIGHT).toFloat()
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
                    box.confidence
                ),
                box.result.rect.left + TEXT_X,
                box.result.rect.top + TEXT_Y,
                paintText!!
            )
        }
    }

    fun setResults(results: ImageDetectionArrayResult?) {
        this.results = results
    }

    companion object {
        private const val TEXT_X = 40
        private const val TEXT_Y = 35
        private const val TEXT_WIDTH = 260
        private const val TEXT_HEIGHT = 50
    }
}