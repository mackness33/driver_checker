package com.example.driverchecker.machinelearning.helpers.classifiers

import com.example.driverchecker.machinelearning.data.IClassification

interface IClassifier<S> {
    val supergroups : Map<S, Set<IClassification<S>>>

    fun asList(outerComparator: Comparator<Set<IClassification<S>>>?, innerComparator: Comparator<IClassification<S>>?) : List<IClassification<S>>
    fun asSortedList(listComparator: Comparator<IClassification<S>>?) : List<IClassification<S>>
    fun asUnsortedList() : List<IClassification<S>>

    fun get (name: String) : IClassification<S>?
    fun get (index: Int) : IClassification<S>?
    fun get (classification: IClassification<S>) : IClassification<S>?

    fun exist (name: String) : Boolean
    fun exist (index: Int) : Boolean
    fun exist (classification: IClassification<S>) : Boolean

    fun getSuperclass (group: S) : Set<IClassification<S>>?

    fun size () : Int
    fun sizeSuperClass () : Int

    fun maxClassesInGroup () : Int
    fun get(index: Int, supergroup: S): IClassification<S>?
    fun exists(index: Int, supergroup: S): Boolean
}