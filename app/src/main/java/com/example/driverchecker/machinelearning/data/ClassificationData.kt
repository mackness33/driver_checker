package com.example.driverchecker.machinelearning.data

import android.graphics.Bitmap
import android.graphics.RectF
import com.example.driverchecker.machinelearning.general.IClassifier

// ---------------------------------- CLASSES ----------------------------------

interface IClassification<Superclass> {
    val name: String
    val index: Int
    val superclass: Superclass
}

data class Classification<Superclass> (
    override val name: String,
    override val index: Int,
    override val superclass: Superclass
) : IClassification<Superclass>

typealias ClassificationSuperclassMap<Superclass> = Map<Superclass, Set<IClassification<Superclass>>>
typealias ClassificationSuperclassList<Superclass> = List<Set<IClassification<Superclass>>>

typealias ClassificationSet<Superclass> = Set<IClassification<Superclass>>
typealias ClassificationList<Superclass> = List<IClassification<Superclass>>