package com.example.driverchecker.ui.views

import android.R.attr
import android.R.attr.startX
import android.R.attr.startY
import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.util.Log
import android.view.View
import com.example.driverchecker.database.entity.ItemEntity
import com.example.driverchecker.machinelearning.data.IImageDetectionFullItem
import com.example.driverchecker.machinelearning.data.IImageDetectionFullOutput
import com.example.driverchecker.utils.ColorManager
import com.example.driverchecker.utils.IColorScale
import kotlin.math.min


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
    private var bitmapDim: Pair<Int, Int> = 0 to 0
    private var withOffset: Boolean = false
    private var actualColorScale: IColorScale = ColorManager.listColors.first()
    private var itemResults: List<DrawableItemResult>? = null
    private var colorList: Set<String>? = setOf("driver", "passenger")

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs) {
        Log.d("ResultView", "Width: $width")
        Log.d("ResultView", "Height: $height")
    }

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

            // final rect must take as information the scale, the vectorScale and the offset
//            mIvScaleX =
//                if (mBitmap.getWidth() > mBitmap.getHeight())
//                    mImageView.getWidth() as Float / mBitmap.getWidth()
//                else
//                    mImageView.getHeight() as Float / mBitmap.getHeight()
//
//            mStartX = (mImageView.getWidth() - mIvScaleX * mBitmap.getWidth())/2;

//            resizedRect.set(
//                item.rect.left * (width / 640) + paintRectangle.strokeWidth,
//                item.rect.top * (height / 640) + paintRectangle.strokeWidth,
//                item.rect.right * (width / 640) - paintRectangle.strokeWidth,
//                item.rect.bottom * (height / 640) - paintRectangle.strokeWidth,
//            )

            resizedRect.set(calculate(item))

//            val rect = Rect(
//                (startX + ivScaleX * imgScaleX * item.rect.left) as Int,
//                (startY + ivScaleY * imgScaleY * item.rect.top) as Int,
//                (startX + ivScaleX * imgScaleX * item.rect.right) as Int,
//                (startY + ivScaleY * imgScaleY * item.rect.bottom) as Int
//            )

            var indexOfGroup = colorList?.indexOfFirst { it.contentEquals(item.group) }
            indexOfGroup = if (indexOfGroup == null || indexOfGroup < 0) 6 else indexOfGroup

            actualColorScale = ColorManager.listFullColors[indexOfGroup]

            drawBox(canvas, item.internalIndex)
            drawPath(canvas)
            drawText(canvas, item)
        }
    }

    private fun calculate (item : DrawableItemResult) : RectF {
        if (withOffset) {
            val vector = min(width.toFloat()/bitmapDim.first, height.toFloat()/bitmapDim.second)
//            val offset =
//                ((width - paintRectangle.strokeWidth) - vector * item.rect.width())/2 to
//                        ((height - paintRectangle.strokeWidth) - vector * item.rect.height())/2
            val offset = 0.0f to 0.0f

            val result = RectF(
                offset.first + vector * item.rect.left + paintRectangle.strokeWidth,
                offset.second + vector * item.rect.top + paintRectangle.strokeWidth,
                offset.first + vector * item.rect.right - paintRectangle.strokeWidth,
                offset.second + vector * item.rect.bottom - paintRectangle.strokeWidth
            )

            return result
        } else {
            val vector = width.toFloat()/bitmapDim.first to height.toFloat()/bitmapDim.second

            val result = RectF(
                vector.first * item.rect.left,
                vector.second * item.rect.top,
                vector.first * item.rect.right,
                vector.second * item.rect.bottom
            )

            return result
        }
    }

    private fun drawText (canvas: Canvas, item: DrawableItemResult) {
        paintText.color = Color.WHITE
        canvas.drawText(
            String.format(
                "%s %.2f%%",
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

    fun setResults (imageDetectionOutputs: IImageDetectionFullOutput<String>?) {
        if (imageDetectionOutputs != null) {
            itemResults = imageDetectionOutputs.listItems.map { DrawableItemResult(it) }
//        bitmapScale = imageDetectionOutputs.input.
            bitmapDim = imageDetectionOutputs.input.input.width to imageDetectionOutputs.input.input.height
        } else {
            itemResults = null
        }
    }

    fun setResults (items: List<ItemEntity>, group: String) {
        itemResults = items.map { DrawableItemResult(it, group) }
    }

    fun setColorSchemes (groupList: Set<String>?) {
        colorList = groupList
    }

    fun maintainRatio(change: Boolean) {
        withOffset = change
    }

    data class DrawableItemResult (
        val rect: RectF,
        val internalIndex: Int,
        val classification: String,
        val confidence: Float,
        val group: String
    ) {
        constructor(imageDetectionItem: IImageDetectionFullItem<String>) : this (
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
        private const val TEXT_X = 10
        private const val TEXT_Y = 35
        private const val TEXT_WIDTH = 300
        private const val TEXT_HEIGHT = 50
    }
}