package com.example.driverchecker.machinelearning.helpers.classifiers

import com.example.driverchecker.machinelearning.data.ImportClassifier
import com.example.driverchecker.machinelearning.data.IClassification

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

    fun load(newDataset: Map<Superclass, Set<IClassification<Superclass>>>?) : Boolean

    fun load(importedJson: ImportClassifier<Superclass>?) : Boolean
}