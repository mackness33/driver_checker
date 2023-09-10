package com.example.driverchecker.ui.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.driverchecker.machinelearning.data.IImageDetectionItem
import com.example.driverchecker.machinelearning.data.IImageDetectionOutput

// Copyright (c) 2020 Facebook, Inc. and its affiliates.
// All rights reserved.
//
// This source code is licensed under the BSD-style license found in the
// LICENSE file in the root directory of this source tree.
class ResultView : View {
    private var paintRectangle: Paint = Paint()
    private var paintText: Paint = Paint()
    private var path: Path = Path()
    private var rectPath: RectF = RectF()
    private var resizedRect: RectF = RectF()
    private var results: IImageDetectionOutput<String>? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    init {
        paintRectangle.color = Color.GREEN
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (results == null) return

        val res: IImageDetectionOutput<String> = results!!

        for (item: IImageDetectionItem<String> in res.listItems) {
            resizedRect.set(
                item.rect.left * (width / 640) - paintRectangle.strokeWidth,
                item.rect.top * (height / 640) - paintRectangle.strokeWidth,
                item.rect.right * (width / 640) - paintRectangle.strokeWidth,
                item.rect.bottom * (height / 640) - paintRectangle.strokeWidth,
            )

            drawBox(canvas)
            drawPath(canvas)
            drawText(canvas, item)
        }
    }

    private fun drawText (canvas: Canvas, item: IImageDetectionItem<String>) {
        paintText.color = Color.WHITE
        paintText.strokeWidth = 0f
        paintText.style = Paint.Style.FILL
        paintText.textSize = 32f
        canvas.drawText(
            String.format(
                "%s %.2f",
                item.classification.name,
                item.confidence
            ),
            resizedRect.left + TEXT_X,
            resizedRect.top + TEXT_Y,
            paintText
        )
    }

    private fun drawPath (canvas: Canvas) {
        path.reset()
        rectPath.set(
            resizedRect.left,
            resizedRect.top,
            resizedRect.left + TEXT_WIDTH,
            resizedRect.top + TEXT_HEIGHT
        )
        path.addRect(rectPath, Path.Direction.CW)
        paintText.color = Color.MAGENTA
        canvas.drawPath(path, paintText)
    }

    private fun drawBox (canvas: Canvas) {
        paintRectangle.strokeWidth = 5f
        paintRectangle.style = Paint.Style.STROKE
        // not working the layoutParams is always at 0. Might need to check the parameter of the layout or holder

        canvas.drawRect(resizedRect, paintRectangle)
    }

    fun setResults(results: IImageDetectionOutput<String>?) {
        this.results = results
    }

    companion object {
        private const val TEXT_X = 40
        private const val TEXT_Y = 35
        private const val TEXT_WIDTH = 260
        private const val TEXT_HEIGHT = 50
    }
}