package com.example.driverchecker

import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix


// items are a list of map with keys the number of the superclass and as value a list of all the classes found
interface IColorScale {
    val scale: List<Int>
    val scaleWithoutMain: List<Int>
    val main: Int?
    var indexMain: Int
}

object BitmapUtils {
    fun rotateBitmap(source: Bitmap, angle: Float): Bitmap? {
        val matrix = Matrix()
        matrix.postRotate(angle)
        return Bitmap.createBitmap(source, 0, 0, source.width, source.height, matrix, true)
    }
}


data class ColorScale (override val scale: List<Int>, override var indexMain: Int = 2) : IColorScale {
    override val main: Int? = if (scale.isNotEmpty() && scale.size <= indexMain) scale[indexMain] else null
    override val scaleWithoutMain: List<Int> = scale.filter { color -> color == scale[indexMain] }
}

open class ColorManager () {
    val mapNonScalableColors: Map<String, IColorScale>
    val listNonScalableColors: List<IColorScale>
        get() = mapNonScalableColors.values.toList()

    val mapColors: Map<String, IColorScale>
    val listColors: List<IColorScale>
        get() = mapColors.values.toList()

    val mapFullColors: Map<String, IColorScale>
        get() = mapColors.plus(mapNonScalableColors)
    val listFullColors: List<IColorScale>
        get() = mapFullColors.values.toList()

    val mapMainColors: Map<String, Int?>
        get() = mapFullColors.mapValues { scaledColor -> scaledColor.value.main }
    val listMainColors: List<Int?>
        get() = mapMainColors.values.toList()

    protected val red: List<Int> = listOf(
        Color.parseColor("#FFCDD2"),
        Color.parseColor("#E57373"),
        Color.parseColor("#F44336"),
        Color.parseColor("#D32F2F"),
        Color.parseColor("#B71C1C")
    )

    protected val green: List<Int> = listOf(
        Color.parseColor("#C8E6C9"),
        Color.parseColor("#81C784"),
        Color.parseColor("#4CAF50"),
        Color.parseColor("#388E3C"),
        Color.parseColor("#1B5E20")
    )

    protected val lime: List<Int> = listOf(
        Color.parseColor("#F0F4C3"),
        Color.parseColor("#DCE775"),
        Color.parseColor("#CDDC39"),
        Color.parseColor("#AFB42B"),
        Color.parseColor("#827717")
    )

    protected val yellow: List<Int> = listOf(
        Color.parseColor("#FFF9C4"),
        Color.parseColor("#FFF176"),
        Color.parseColor("#FFEB3B"),
        Color.parseColor("#FBC02D"),
        Color.parseColor("#F57F17")
    )

    protected val orange: List<Int> = listOf(
        Color.parseColor("#FFE0B2"),
        Color.parseColor("#FFB74D"),
        Color.parseColor("#FF9800"),
        Color.parseColor("#F57C00"),
        Color.parseColor("#E65100")
    )

    protected val blue: List<Int> = listOf(
        Color.parseColor("#BBDEFB"),
        Color.parseColor("#64B5F6"),
        Color.parseColor("#2196F3"),
        Color.parseColor("#1976D2"),
        Color.parseColor("#0D47A1")
    )

    protected val grey: List<Int> = listOf(
        Color.parseColor("#F5F5F5"),
        Color.parseColor("#E0E0E0"),
        Color.parseColor("#9E9E9E"),
        Color.parseColor("#616161"),
        Color.parseColor("#212121")
    )

    protected val blueGray: List<Int> = listOf(
        Color.parseColor("#CFD8DC"),
        Color.parseColor("#90A4AE"),
        Color.parseColor("#607D8B"),
        Color.parseColor("#455A64"),
        Color.parseColor("#263238")
    )

    protected val brown: List<Int> = listOf(
        Color.parseColor("#D7CCC8"),
        Color.parseColor("#A1887F"),
        Color.parseColor("#795548"),
        Color.parseColor("#5D4037"),
        Color.parseColor("#3E2723")
    )

    protected val cyan: List<Int> = listOf(
        Color.parseColor("#B2EBF2"),
        Color.parseColor("#4DD0E1"),
        Color.parseColor("#00BCD4"),
        Color.parseColor("#0097A7"),
        Color.parseColor("#006064")
    )

    protected val teal: List<Int> = listOf(
        Color.parseColor("#B2DFDB"),
        Color.parseColor("#4DB6AC"),
        Color.parseColor("#009688"),
        Color.parseColor("#00796B"),
        Color.parseColor("#004D40")
    )

    protected val indigo: List<Int> = listOf(
        Color.parseColor("#C5CAE9"),
        Color.parseColor("#7986CB"),
        Color.parseColor("#3F51B5"),
        Color.parseColor("#303F9F"),
        Color.parseColor("#1A237E")
    )

    protected val purple: List<Int> = listOf(
        Color.parseColor("#E1BEE7"),
        Color.parseColor("#BA68C8"),
        Color.parseColor("#9C27B0"),
        Color.parseColor("#7B1FA2"),
        Color.parseColor("#4A148C")
    )

    protected val pink: List<Int> = listOf(
        Color.parseColor("#F8BBD0"),
        Color.parseColor("#F06292"),
        Color.parseColor("#E91E63"),
        Color.parseColor("#C2185B"),
        Color.parseColor("#880E4F")
    )

    protected val black: List<Int> = listOf(
        Color.parseColor("#000000"),
        Color.parseColor("#000000"),
        Color.parseColor("#000000"),
        Color.parseColor("#000000"),
        Color.parseColor("#000000")
    )

    protected val white: List<Int> = listOf(
        Color.parseColor("#FFFFFF"),
        Color.parseColor("#FFFFFF"),
        Color.parseColor("#FFFFFF"),
        Color.parseColor("#FFFFFF"),
        Color.parseColor("#FFFFFF")
    )

    init {
        mapColors = mapOf(
            "green" to ColorScale(green),
            "blue" to ColorScale(blue),
            "orange" to ColorScale(orange),
            "teal" to ColorScale(teal),
            "brown" to ColorScale(brown),
            "indigo" to ColorScale(indigo),
            "red" to ColorScale(red),
            "lime" to ColorScale(lime),
            "pink" to ColorScale(pink),
            "blue_gray" to ColorScale(blueGray),
            "yellow" to ColorScale(yellow),
            "cyan" to ColorScale(cyan),
            "grey" to ColorScale(grey),
            "purple" to ColorScale(purple)
        )
        mapNonScalableColors = mapOf(
            "transparent" to ColorScale(emptyList()),
            "black" to ColorScale(black),
            "white" to ColorScale(white)
        )
    }

//    val base: List<Int> = listOf(
//        Color.parseColor("#),
//        Color.parseColor("#),
//        Color.parseColor("#),
//        Color.parseColor("#),
//        Color.parseColor("#)
//    )
}

