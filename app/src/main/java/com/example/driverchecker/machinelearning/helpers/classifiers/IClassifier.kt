package com.example.driverchecker.machinelearning.helpers.classifiers

import com.example.driverchecker.machinelearning.data.ClassificationList
import com.example.driverchecker.machinelearning.data.ClassificationSet
import com.example.driverchecker.machinelearning.data.ClassificationSupergroupMap
import com.example.driverchecker.machinelearning.data.IClassification

interface IClassifier<S> {
    val supergroups : ClassificationSupergroupMap<S>

    fun asList(outerComparator: Comparator<ClassificationSet<S>>?, innerComparator: Comparator<IClassification<S>>?) : ClassificationList<S>
    fun asSortedList(listComparator: Comparator<IClassification<S>>?) : ClassificationList<S>
    fun asUnsortedList() : ClassificationList<S>

    fun get (name: String) : IClassification<S>?
    fun get (index: Int) : IClassification<S>?
    fun get (classification: IClassification<S>) : IClassification<S>?

    fun exist (name: String) : Boolean
    fun exist (index: Int) : Boolean
    fun exist (classification: IClassification<S>) : Boolean

    fun getSuperclass (group: S) : ClassificationSet<S>?

    fun size () : Int
    fun sizeSuperClass () : Int

    fun maxClassesInGroup () : Int
    fun get(index: Int, supergroup: S): IClassification<S>?
    fun exists(index: Int, supergroup: S): Boolean
}