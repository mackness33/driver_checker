package com.example.driverchecker.ui.views

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import com.example.driverchecker.database.ItemEntity
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
    private var itemResults: List<DrawableItemResult>? = null
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
        paintText.textSize = 27f
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        if (itemResults == null) return

        itemResults?.forEach { item ->
            resizedRect.set(
                item.rect.left * (width / 640) - paintRectangle.strokeWidth,
                item.rect.top * (height / 640) - paintRectangle.strokeWidth,
                item.rect.right * (width / 640) - paintRectangle.strokeWidth,
                item.rect.bottom * (height / 640) - paintRectangle.strokeWidth,
            )

            actualColorScale = ColorManager.listFullColors[colorList?.indexOfFirst { it.contentEquals(item.group) } ?: 6]

            drawBox(canvas, item.internalIndex)
            drawPath(canvas)
            drawText(canvas, item)
        }
    }

    private fun drawText (canvas: Canvas, item: DrawableItemResult) {
        paintText.color = Color.WHITE
        canvas.drawText(
            String.format(
                "%s %.2f",
                item.classification,
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
        itemResults = imageDetectionOutputs?.listItems?.map { DrawableItemResult(it) }
    }

    fun setResults (items: List<ItemEntity>, group: String) {
        itemResults = items.map { DrawableItemResult(it, group) }
    }

    fun setColorSchemes (groupList: Set<String>?) {
        colorList = groupList
    }

    data class DrawableItemResult (
        val rect: RectF,
        val internalIndex: Int,
        val classification: String,
        val confidence: Float,
        val group: String
    ) {
        constructor(imageDetectionItem: IImageDetectionItem<String>) : this (
            rect = imageDetectionItem.rect,
            internalIndex = imageDetectionItem.classification.internalIndex,
            classification = imageDetectionItem.classification.name,
            confidence = imageDetectionItem.confidence,
            group = imageDetectionItem.classification.supergroup
        )

        constructor(item: ItemEntity, group: String) : this (
            rect = RectF(item.rect.left, item.rect.top, item.rect.right, item.rect.bottom),
            internalIndex = item.internalIndex,
            classification = item.classification,
            confidence = item.confidence,
            group = group
        )
    }

    companion object {
        private const val TEXT_X = 40
        private const val TEXT_Y = 35
        private const val TEXT_WIDTH = 260
        private const val TEXT_HEIGHT = 50
    }
}