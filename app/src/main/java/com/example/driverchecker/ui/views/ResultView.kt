package com.example.driverchecker.ui.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.driverchecker.machinelearning.data.IImageDetectionItemOld
import com.example.driverchecker.machinelearning.data.IImageDetectionResultOld

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
    private var results: IImageDetectionResultOld<String>? = null

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

        val res: IImageDetectionResultOld<String> = results!!

        for (item: IImageDetectionItemOld<String> in res.listItems) {
            drawBox(canvas, item.rect)
            drawPath(canvas, item)
            drawText(canvas, item)
        }
    }

    private fun drawText (canvas: Canvas, item: IImageDetectionItemOld<String>) {
        paintText!!.color = Color.WHITE
        paintText!!.strokeWidth = 0f
        paintText!!.style = Paint.Style.FILL
        paintText!!.textSize = 32f
        canvas.drawText(
            String.format(
                "%s %.2f",
                item.classification.name,
                item.confidence
            ),
            item.rect.left + TEXT_X,
            item.rect.top + TEXT_Y,
            paintText!!
        )
    }

    private fun drawPath (canvas: Canvas, box: IImageDetectionItemOld<String>) {
        path?.reset()
        rect?.set(
            box.rect.left,
            box.rect.top,
            box.rect.left + TEXT_WIDTH,
            box.rect.top + TEXT_HEIGHT
        )
        path?.addRect(rect!!, Path.Direction.CW)
        paintText!!.color = Color.MAGENTA
        canvas.drawPath(path!!, paintText!!)
    }

    private fun drawBox (canvas: Canvas, rect: RectF) {
        paintRectangle!!.strokeWidth = 5f
        paintRectangle!!.style = Paint.Style.STROKE
        canvas.drawRect(rect, paintRectangle!!)
    }

    fun setResults(results: IImageDetectionResultOld<String>?) {
        this.results = results
    }

    companion object {
        private const val TEXT_X = 40
        private const val TEXT_Y = 35
        private const val TEXT_WIDTH = 260
        private const val TEXT_HEIGHT = 50
    }
}