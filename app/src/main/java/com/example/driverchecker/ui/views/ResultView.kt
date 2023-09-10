package com.example.driverchecker.ui.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.driverchecker.utils.ColorManager
import com.example.driverchecker.utils.IColorScale
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
    private var actualColorScale: IColorScale = ColorManager.listColors.first()
    private var outputs: IImageDetectionOutput<String>? = null
    private var colorList: Set<String>? = null

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)

    init {
        paintRectangle.color = Color.TRANSPARENT
        paintRectangle.strokeWidth = 5f
        paintRectangle.style = Paint.Style.STROKE

        paintText.color = Color.WHITE
        paintText.strokeWidth = 0f
        paintText.style = Paint.Style.FILL
        paintText.textSize = 30f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (outputs == null) return

        val res: IImageDetectionOutput<String> = outputs!!

        for (item: IImageDetectionItem<String> in res.listItems) {
            resizedRect.set(
                item.rect.left * (width / 640) - paintRectangle.strokeWidth,
                item.rect.top * (height / 640) - paintRectangle.strokeWidth,
                item.rect.right * (width / 640) - paintRectangle.strokeWidth,
                item.rect.bottom * (height / 640) - paintRectangle.strokeWidth,
            )

            actualColorScale = ColorManager.listFullColors[colorList?.indexOfFirst { it.contentEquals(item.classification.supergroup) } ?: 6]

            drawBox(canvas, item.classification.internalIndex)
            drawPath(canvas)
            drawText(canvas, item)
        }
    }

    private fun drawText (canvas: Canvas, item: IImageDetectionItem<String>) {
        paintText.color = Color.WHITE
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
        paintText.color = actualColorScale.scale[2]
        canvas.drawPath(path, paintText)
    }

    private fun drawBox (canvas: Canvas, classIndex: Int) {
        // not working the layoutParams is always at 0. Might need to check the parameter of the layout or holder
        paintRectangle.color = actualColorScale.scale[classIndex]
        canvas.drawRect(resizedRect, paintRectangle)
    }

    fun setResults (imageDetectionOutputs: IImageDetectionOutput<String>?) {
        outputs = imageDetectionOutputs
    }

    fun setColorScheme (groupList: Set<String>?) {
        colorList = groupList
    }

    companion object {
        private const val TEXT_X = 40
        private const val TEXT_Y = 35
        private const val TEXT_WIDTH = 260
        private const val TEXT_HEIGHT = 50
    }
}