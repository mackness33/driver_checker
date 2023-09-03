package com.example.driverchecker.machinelearning.helpers.classifiers

import com.example.driverchecker.machinelearning.data.ClassificationList
import com.example.driverchecker.machinelearning.data.ClassificationSet
import com.example.driverchecker.machinelearning.data.ClassificationSupergroupMap
import com.example.driverchecker.machinelearning.data.IClassification

interface IClassifier<Superclass> {
    val superclasses : ClassificationSupergroupMap<Superclass>

    fun asList(outerComparator: Comparator<ClassificationSet<Superclass>>?, innerComparator: Comparator<IClassification<Superclass>>?) : ClassificationList<Superclass>
    fun asSortedList(listComparator: Comparator<IClassification<Superclass>>?) : ClassificationList<Superclass>
    fun asUnsortedList() : ClassificationList<Superclass>

    fun get (name: String) : IClassification<Superclass>?
    fun get (index: Int) : IClassification<Superclass>?
    fun get (classification: IClassification<Superclass>) : IClassification<Superclass>?

    fun exist (name: String) : Boolean
    fun exist (index: Int) : Boolean
    fun exist (classification: IClassification<Superclass>) : Boolean

    fun getSuperclass (group: Superclass) : ClassificationSet<Superclass>?

    fun size () : Int
    fun sizeSuperClass () : Int

    fun maxClassesInGroup () : Int
    fun get(index: Int, supergroup: S): IClassification<S>?
    fun exists(index: Int, supergroup: S): Boolean
}