package com.example.driverchecker.machinelearning.general

import com.example.driverchecker.machinelearning.data.*

interface IMutableClassifier<Superclass> : IClassifier<Superclass> {
    fun add(name: String, group: Superclass)
    fun append(name: String, group: Superclass)

    fun put(newSuperclass: Set<IClassification<Superclass>>, group: Superclass)
    fun putIfAbsent(newSuperclass: Set<IClassification<Superclass>>, group: Superclass)

    fun clear(group: Superclass?)

    fun remove (name: String) : Boolean
    fun remove (index: Int) : Boolean

    fun delete (name: String) : Boolean
    fun delete (index: Int) : Boolean

    fun load(newDataset: ClassificationSuperclassMap<Superclass>?) : Boolean

    fun load(importedJson: BaseClassifier<Superclass>?) : Boolean
}